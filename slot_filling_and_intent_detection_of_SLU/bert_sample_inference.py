# BERT sample model loading and inference to test on device conversion pipeline

from transformers.modeling_bert import BertForTokenClassification
import torch
import time
import numpy as np

import argparse
import random
import torch
import torch.nn as nn
import torch.optim as optim
import numpy as np
import os, sys, time
import logging
import gc

install_path = os.path.abspath(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
sys.path.append(install_path)

#from pytorch_pretrained_bert import BertTokenizer, BertModel
#from pytorch_pretrained_bert.optimization import BertAdam, WarmupLinearSchedule
from transformers import BertTokenizer, BertModel, XLNetTokenizer, XLNetModel 
from transformers import AdamW, get_linear_schedule_with_warmup
from models.optimization import BertAdam
from utils.bert_xlnet_inputs import prepare_inputs_for_bert_xlnet

import models.slot_tagger_and_intent_detector_with_pure_transformer as joint_transformer

import utils.vocab_reader as vocab_reader
import utils.data_reader_for_elmo as data_reader
import utils.read_wordEmb as read_wordEmb
import utils.util as util
import utils.acc as acc

MODEL_CLASSES = {
        'bert': (BertModel, BertTokenizer),
        'xlnet': (XLNetModel, XLNetTokenizer),
        }

def save_print_torchscipt(config_path, output_zip):

    print(torch.__version__)
    device = torch.device("cpu")

    pretrained_model_class, tokenizer_class = MODEL_CLASSES['bert']
    tokenizer = tokenizer_class.from_pretrained('mrm8488/bert-tiny-finetuned-squadv2')
    pretrained_model = pretrained_model_class.from_pretrained('mrm8488/bert-tiny-finetuned-squadv2')
    #print(pretrained_model.config)

    model_tag_and_class = joint_transformer.Transformers_joint_slot_and_intent('bert', pretrained_model, tokenizer,24, 13, dropout=0.1, device=device, multi_class=False, task_st='NN', task_sc='CLS')

    model_tag_and_class = model_tag_and_class.to('cpu')

    model_tag_and_class.load_model('exp/test_tiny/model')

    tag_to_idx, idx_to_tag = vocab_reader.read_vocab_file('exp/test_tiny/vocab'+'.tag', bos_eos=False, no_pad=True, no_unk=True)
    class_to_idx, idx_to_class = vocab_reader.read_vocab_file('exp/test_tiny/vocab'+'.class', bos_eos=False, no_pad=True, no_unk=True)

    print('------------------------------------------------------conversion-------------------------------------------------------')
    print(tag_to_idx,idx_to_tag)
    print(class_to_idx, idx_to_class)
    print('-------------------------------------------------------------------------------------------------------------------------')

    test_feats, test_tags, test_class = data_reader.read_seqtag_data_with_class('data/appliance-one-test-sample/test', tag_to_idx, class_to_idx, multiClass=False, keep_order=True, lowercase=False)
    
    print(test_feats,test_tags,test_class)

    exp_path = 'exp/test_tiny'


    data_feats, data_tags, data_class, output_path = test_feats['data'], test_tags['data'], test_class['data'], os.path.join(exp_path, 'test.eval')

    weight_mask = torch.ones(len(tag_to_idx), device=device)
    weight_mask[tag_to_idx['<pad>']] = 0
    tag_loss_function = nn.NLLLoss(weight=weight_mask, reduction='sum')
    class_loss_function = nn.NLLLoss(reduction='sum')
    data_index = np.arange(len(data_feats))
    losses = []
    TP, FP, FN, TN = 0.0, 0.0, 0.0, 0.0
    TP2, FP2, FN2, TN2 = 0.0, 0.0, 0.0, 0.0
    with open(output_path, 'w') as f, torch.no_grad():
        for j in range(0, len(data_index), 32):

            words, tags, raw_tags, classes, raw_classes, lens, line_nums = data_reader.get_minibatch_with_class(data_feats, data_tags, data_class, tag_to_idx, class_to_idx, data_index, j, 32, add_start_end=False, multiClass=False, keep_order=True, enc_dec_focus=False, device=device)

            print('---------------------before------------------------------')
            print(words, tags, raw_tags, classes, raw_classes, lens, line_nums)
            print('-------------------------tensor conversion using prepare_inputs_for_bert_xlnet---------------------------------')

            inputs = prepare_inputs_for_bert_xlnet(words, lens, tokenizer, 
                    cls_token_at_end=bool('bert' in ['xlnet']),  # xlnet has a cls token at the end
                    cls_token=tokenizer.cls_token,
                    sep_token=tokenizer.sep_token,
                    cls_token_segment_id=2 if 'bert' in ['xlnet'] else 0,
                    pad_on_left=bool('bert' in ['xlnet']), # pad on the left for xlnet
                    pad_token_segment_id=4 if 'bert' in ['xlnet'] else 0,
                    device=device)

            print(words, lens, tokenizer, bool('bert' in ['xlnet']),2 if 'bert' in ['xlnet'] else 0,bool('bert' in ['xlnet']),4 if 'bert' in ['xlnet'] else 0)

            print(inputs)

            inputs['lengths'] = torch.IntTensor(lens)

            traced = torch.jit.trace(model_tag_and_class, (inputs))

            output_zip = 'ziptemp'

            traced.save(output_zip)

            loaded = torch.jit.load(output_zip)

            print(loaded)
            print('------')
            print(loaded.code)

            print('--------------------tensor probabilities for tag and class using model_tag_and_class---------------------------------')

            #if opt.task_st
            tag_scores, class_scores = model_tag_and_class(inputs)
            print(tag_scores,class_scores)

            print('---------------------after evaluating the tensors--------------------')

            tag_loss = tag_loss_function(tag_scores.contiguous().view(-1, len(tag_to_idx)), tags.view(-1))
            top_pred_slots = tag_scores.data.cpu().numpy().argmax(axis=-1)
            print(top_pred_slots)

            #if opt.task_sc:
            class_loss = class_loss_function(class_scores, classes)
            snt_probs = class_scores.data.cpu().numpy().argmax(axis=-1)
            print(snt_probs)

            losses.append([tag_loss.item()/sum(lens), class_loss.item()/len(lens)])

            print("-----------------------reconstruct ----------------------------")

            for idx, pred_line in enumerate(top_pred_slots):
                length = lens[idx]
                pred_seq = [idx_to_tag[tag] for tag in pred_line][:length]
                print(pred_seq)
                input_line = words[idx]
                print(input_line)
                word_tag_line = [input_line[_idx]+':'+pred_seq[_idx] for _idx in range(len(input_line)) if pred_seq[_idx] != 'O']
                print('slots:',word_tag_line)
                pred_class = idx_to_class[snt_probs[idx]]
                print('intent:',pred_class)

            print("--------------------------------------------------------------")



    print('end')

def save_torchscipt(exp_path, output_zip):

    print(torch.__version__)
    device = torch.device("cpu")

    pretrained_model_class, tokenizer_class = MODEL_CLASSES['bert']
    tokenizer = tokenizer_class.from_pretrained('mrm8488/bert-tiny-finetuned-squadv2')
    pretrained_model = pretrained_model_class.from_pretrained('mrm8488/bert-tiny-finetuned-squadv2')
    #print(pretrained_model.config)

    print(tokenizer)

    model_tag_and_class = joint_transformer.Transformers_joint_slot_and_intent('bert', pretrained_model, tokenizer, 24, 13, dropout=0.1, device=device, multi_class=False, task_st='NN', task_sc='CLS')

    model_tag_and_class = model_tag_and_class.to('cpu')

    print(model_tag_and_class.state_dict().keys())

    model_tag_and_class.load_model(exp_path+'/model')

    tag_to_idx, idx_to_tag = vocab_reader.read_vocab_file(exp_path+'/vocab'+'.tag', bos_eos=False, no_pad=True, no_unk=True)
    class_to_idx, idx_to_class = vocab_reader.read_vocab_file(exp_path+'/vocab'+'.class', bos_eos=False, no_pad=True, no_unk=True)

    test_feats, test_tags, test_class = data_reader.read_seqtag_data_with_class('data/appliance-one-test-sample/test', tag_to_idx, class_to_idx, multiClass=False, keep_order=True, lowercase=False)

    #exp_path = 'exp\\test_tiny'


    data_feats, data_tags, data_class, output_path = test_feats['data'], test_tags['data'], test_class['data'], os.path.join(exp_path, 'test.eval')

    print(test_feats,test_tags, test_class)


    weight_mask = torch.ones(len(tag_to_idx), device=device)
    weight_mask[tag_to_idx['<pad>']] = 0
    tag_loss_function = nn.NLLLoss(weight=weight_mask, reduction='sum')
    class_loss_function = nn.NLLLoss(reduction='sum')
    data_index = np.arange(len(data_feats))
    losses = []
    with open(output_path, 'w') as f, torch.no_grad():
        for j in range(0, len(data_index), 32):

            words, tags, raw_tags, classes, raw_classes, lens, line_nums = data_reader.get_minibatch_with_class(data_feats, data_tags, data_class, tag_to_idx, class_to_idx, data_index, j, 32, add_start_end=False, multiClass=False, keep_order=True, enc_dec_focus=False, device=device)

            print("words")
            print(words)


            inputs = prepare_inputs_for_bert_xlnet(words, lens, tokenizer, 
                    cls_token_at_end=bool('bert' in ['xlnet']),  # xlnet has a cls token at the end
                    cls_token=tokenizer.cls_token,
                    sep_token=tokenizer.sep_token,
                    cls_token_segment_id=2 if 'bert' in ['xlnet'] else 0,
                    pad_on_left=bool('bert' in ['xlnet']), # pad on the left for xlnet
                    pad_token_segment_id=4 if 'bert' in ['xlnet'] else 0,
                    device=device)

            inputs['lengths'] = torch.LongTensor(lens)

            print(inputs)

            #print(words)

            #print(words[0])


            sentence_ = ' '.join(words[0])

            #print([[ord(c) for c in w] for w in words[0]])

            #print([ord(c) for c in sentence_])

            asciiword = [ord(c) for c in sentence_]

            ascii_tensor = torch.LongTensor(asciiword)

            #print(inputs)

            # long_tensor_temp = torch.LongTensor(1)

            # print(dictionary[long_tensor_temp.data[0]])


            traced = torch.jit.trace(model_tag_and_class, (ascii_tensor, torch.LongTensor([17]), inputs['lengths'],inputs['mask'], inputs['segments'],inputs['tokens'],inputs['selects'],inputs['copies']))

            #output_zip = 'ziptemp'

            output_zip = 'output/model_tiny_9_1.pt'

            traced.save(output_zip)

            loaded = torch.jit.load(output_zip)

            sample = ['can','i', 'use', 'porcelain', 'in', 'the', 'oven']

            sample = ['will','i','be','able','to','use','procelain','in','the','oven']

            sample = ['what', 'does', 'c-f0', 'code', 'mean', 'on', 'my', 'oven']

            sample = ['how','do','i','use','the','reheat','feature','in','the','microwave']

            sentence_ = ' '.join(sample)

            asciiword = [ord(c) for c in sentence_]

            #print(asciiword)

            tensor_question = torch.LongTensor(asciiword)

            lens = [len(sample)]

            inputs_test = prepare_inputs_for_bert_xlnet([sample], lens, tokenizer, 
                    cls_token_at_end=bool('bert' in ['xlnet']),  # xlnet has a cls token at the end
                    cls_token=tokenizer.cls_token,
                    sep_token=tokenizer.sep_token,
                    cls_token_segment_id=2 if 'bert' in ['xlnet'] else 0,
                    pad_on_left=bool('bert' in ['xlnet']), # pad on the left for xlnet
                    pad_token_segment_id=4 if 'bert' in ['xlnet'] else 0,
                    device=device)

            print(inputs_test)

            print("tokens:------",inputs_test['tokens'])

            inputs_test['lengths'] = torch.LongTensor(lens)            

            tag_scores, class_scores = loaded.forward(tensor_question,torch.LongTensor([700]),inputs_test['lengths'],inputs_test['mask'], inputs_test['segments'],inputs_test['tokens'],inputs_test['selects'],inputs_test['copies'])

            #print('time tensor', time_tensor.item())

            #print(testing_variable, sentence_output,inputs_output,tokens_tensor,lens_output,new_tokenized_sentence)

            #print("sentence_output",sentence_output)

            #print(asciiword_return)


            #asciiword = [chr(c) for c in sentence_output.tolist()]

        	#print(asciiword)


            #print(tag_scores,class_scores)

            top_pred_slots = tag_scores.data.cpu().numpy().argmax(axis=-1)
            snt_probs = class_scores.data.cpu().numpy().argmax(axis=-1)
            print(top_pred_slots)
            print(snt_probs)

            lens = [len(words[0])]

            print("-----------------------reconstruct ----------------------------")

            words = [sample]

            for idx, pred_line in enumerate(top_pred_slots):
                length = lens[idx]
                pred_seq = [idx_to_tag[tag] for tag in pred_line][:length]
                print(pred_seq)
                input_line = words[idx]
                print(input_line)
                word_tag_line = [input_line[_idx]+':'+pred_seq[_idx] for _idx in range(len(input_line)) if pred_seq[_idx] != 'O']
                print('slots:',word_tag_line)
                pred_class = idx_to_class[snt_probs[idx]]
                print('intent:',pred_class)




            # print(loaded)
            # print('------')
            # print(loaded.code)


def save_pt_entire(config_path, output_pt):
    model = BertForTokenClassification.from_pretrained(config_path, num_labels=10)
    torch.save(model, output_pt)

    # Load
    model = torch.load(output_pt)
    model.eval()

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

    input_ids = torch.tensor([[2148, 3933, 7987, 16975, 16258, 9317, 1010, 2251, 2321, 1010,
                               12609, 5718, 1024, 2382, 7610, 1011, 5641, 1024, 3429, 7610]]).to(
        device)

    model.to(device)
    with torch.no_grad():
        output = model(input_ids)

    label_indices = np.argmax(output[0].to('cpu').numpy(), axis=2)

    print("max logit index for probability vector for each input ID: ")
    print(label_indices)

def sample_bert_inference(exp_path,output_zip):

    print(torch.__version__)
    device = torch.device("cpu")
    pretrained_model_class, tokenizer_class = MODEL_CLASSES['bert']
    tokenizer = tokenizer_class.from_pretrained('mrm8488/bert-tiny-finetuned-squadv2')
    pretrained_model = pretrained_model_class.from_pretrained('mrm8488/bert-tiny-finetuned-squadv2')
    #print(pretrained_model.config)
    model_tag_and_class = joint_transformer.Transformers_joint_slot_and_intent('bert', pretrained_model, 24, 13, dropout=0.1, device=device, multi_class=False, task_st='NN', task_sc='CLS')
    model_tag_and_class = model_tag_and_class.to('cpu')
    model_tag_and_class.load_model('exp\\test_tiny\\model')
    tag_to_idx, idx_to_tag = vocab_reader.read_vocab_file('exp/test_tiny/vocab'+'.tag', bos_eos=False, no_pad=True, no_unk=True)
    class_to_idx, idx_to_class = vocab_reader.read_vocab_file('exp/test_tiny/vocab'+'.class', bos_eos=False, no_pad=True, no_unk=True)

    test_feats, test_tags, test_class = data_reader.read_seqtag_data_with_class('data/appliance-one-test-sample/test', tag_to_idx, class_to_idx, multiClass=False, keep_order=True, lowercase=False)

    #exp_path = 'exp\\test_tiny'

    data_feats, data_tags, data_class, output_path = test_feats['data'], test_tags['data'], test_class['data'], os.path.join(exp_path, 'test.eval')

    weight_mask = torch.ones(len(tag_to_idx), device=device)
    weight_mask[tag_to_idx['<pad>']] = 0
    tag_loss_function = nn.NLLLoss(weight=weight_mask, reduction='sum')
    class_loss_function = nn.NLLLoss(reduction='sum')
    data_index = np.arange(len(data_feats))
    losses = []

    with open(output_path, 'w') as f, torch.no_grad():
        for j in range(0, len(data_index), 32):

            words, tags, raw_tags, classes, raw_classes, lens, line_nums = data_reader.get_minibatch_with_class(data_feats, data_tags, data_class, tag_to_idx, class_to_idx, data_index, j, 32, add_start_end=False, multiClass=False, keep_order=True, enc_dec_focus=False, device=device)

            inputs = prepare_inputs_for_bert_xlnet(words, lens, tokenizer, 
                    cls_token_at_end=bool('bert' in ['xlnet']),  # xlnet has a cls token at the end
                    cls_token=tokenizer.cls_token,
                    sep_token=tokenizer.sep_token,
                    cls_token_segment_id=2 if 'bert' in ['xlnet'] else 0,
                    pad_on_left=bool('bert' in ['xlnet']), # pad on the left for xlnet
                    pad_token_segment_id=4 if 'bert' in ['xlnet'] else 0,
                    device=device)

            print('tensors: ---------------------------------------------------------------------')
            print(inputs)

            inputs['lengths'] = torch.IntTensor(lens)

            traced = torch.jit.trace(model_tag_and_class, (inputs))

            #output_zip = 'ziptemp'

            traced.save(output_zip)

            loaded = torch.jit.load(output_zip)

            #if opt.task_st
            tag_scores, class_scores = model_tag_and_class(inputs)

            # print('After evaluating the tensors: --------------------')

            # print(tag_scores,class_scores)

            print('---------------------after evaluating the tensors--------------------')

            tag_loss = tag_loss_function(tag_scores.contiguous().view(-1, len(tag_to_idx)), tags.view(-1))
            top_pred_slots = tag_scores.data.cpu().numpy().argmax(axis=-1)
            print("slot prediction output tensor",top_pred_slots)

            #if opt.task_sc:
            class_loss = class_loss_function(class_scores, classes)
            snt_probs = class_scores.data.cpu().numpy().argmax(axis=-1)
            print("intent prediction output tensor",snt_probs)

            losses.append([tag_loss.item()/sum(lens), class_loss.item()/len(lens)])


if __name__ == '__main__':
    # model_path = '/media/disks/disk2/v.srinivasan/bert_poster/models/uncased_L-4_H-256_A-8-large-training-bert-none'
    #model_path = 'uncased_L_2_H_256_A_4'

    # sample_bert_inference(model_path)

    #output_zip = 'uncased_L_2_H_256_A_4\\mars_bert.pt'

    #save_torchscipt(model_path, output_zip)

    model_path = 'output/model_tiny_9_1.pt'
    exp_path = 'exp/model_tiny_default'

    save_torchscipt(exp_path,model_path)

    # output_pt = '/media/disks/disk2/v.srinivasan/datasets/poster_mdc/torch_script_models/uncased_L-4_H-256_A-8-large-training-bert-none.pt'
    # save_pt_entire(model_path, output_pt)



# raw output logits from model
# <class 'tuple'>: (tensor([[[ 4.7271, -1.1582,  1.8821, -2.1353, -1.4393, -1.6623, -0.5995,
#           -1.4578,  0.6089, -0.9777],
#          [ 2.0963,  4.4132,  0.3787,  0.1611, -1.8020, -2.3625, -1.4344,
#           -1.1800, -0.4566, -1.0974],
#          [ 0.6891,  4.7750, -0.3429,  0.8823, -1.4038, -1.9839, -1.3285,
#           -0.7978, -0.4600, -1.1658],
#          [ 2.1217,  4.4413,  0.2838,  0.5113, -1.7407, -2.1693, -1.4098,
#           -0.9567, -0.9419, -0.6965],
#          [ 0.2583,  5.0838, -0.8756,  0.7270, -1.1932, -1.8138, -1.3960,
#           -0.9951, -0.1668, -1.3156],
#          [-1.4237, -0.1073, -1.8955, -1.8439,  4.9684,  0.4336, -1.3899,
#           -1.7876,  1.1784, -1.3331],
#          [-0.8309, -1.2054, -1.9698, -1.3277, -0.9960,  5.6136, -2.0083,
#           -1.3577,  1.5612, -1.9090],
#          [-0.8857, -1.2573, -1.9539, -1.4615, -0.5932,  5.6854, -2.0031,
#           -1.4554,  1.4376, -1.8260],
#          [-0.8780, -1.2486, -1.8908, -1.3500, -0.8095,  5.6745, -2.0155,
#           -1.4205,  1.4584, -1.8341],
#          [-0.7874, -1.2317, -1.8337, -1.2239, -1.2240,  5.6230, -1.9777,
#           -1.3817,  1.5032, -1.8727],
#          [-0.7721, -1.2331, -1.8323, -1.2532, -1.0467,  5.6647, -1.9691,
#           -1.5109,  1.4528, -1.8437],
#          [-1.0096, -1.3088, -1.8910, -1.4128, -0.1991,  5.7136, -2.0057,
#           -1.5489,  1.3557, -1.7237],
#          [-0.8414, -1.3235, -1.8364, -1.3403, -0.7395,  5.6898, -1.9614,
#           -1.5030,  1.4919, -1.8093],
#          [-0.8948, -1.2517, -1.8314, -1.2834, -0.6805,  5.7171, -1.9751,
#           -1.5125,  1.4047, -1.7670],
#          [-0.9425, -1.2960, -1.8009, -1.3030, -0.6570,  5.7126, -1.9608,
#           -1.5232,  1.4541, -1.7508],
#          [-1.0247, -1.3034, -1.8791, -1.2928, -0.5438,  5.6390, -2.0600,
#           -1.5708,  1.5934, -1.8344],
#          [-1.1094, -1.3597, -1.8372, -1.3455, -0.3473,  5.6493, -2.0546,
#           -1.6068,  1.6178, -1.8001],
#          [-0.9566, -1.3399, -1.8286, -1.3434, -0.6700,  5.6266, -2.0164,
#           -1.5541,  1.7301, -1.8904],
#          [-0.9782, -1.3442, -1.8009, -1.3495, -0.5436,  5.6534, -2.0153,
#           -1.5639,  1.6088, -1.8206],
#          [-0.9593, -1.2946, -1.7966, -1.3329, -0.7492,  5.6562, -1.9864,
#           -1.5520,  1.6284, -1.8115]]], device='cuda:0'),)
