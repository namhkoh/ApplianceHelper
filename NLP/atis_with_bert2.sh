#!/bin/bash

source ./path.sh

task_slot_filling=slot_tagger_with_focus #slot_tagger, slot_tagger_with_crf, slot_tagger_with_focus
task_intent_detection=hiddenAttention # none, hiddenAttention, hiddenCNN, maxPooling, 2tails
balance_weight=0.5

pretrained_model_type=bert
pretrained_model_name=bert-base-uncased #bert-base-cased #bert-large-uncased-whole-word-masking #bert-base-uncased

dataroot=data/atis-2
dataset=atis
outpath=output

lstm_hidden_size=200 # 100, 200
lstm_layers=1
slot_tag_embedding_size=100  ## for slot_tagger_with_focus
batch_size=32 # 16, 32

optimizer=adam # bertadam, adamw
learning_rate=5e-5 # 1e-5, 5e-5, 1e-4, 1e-3
max_norm_of_gradient_clip=1 # working for adamw
dropout_rate=0.1 # 0.1, 0.5
testing=true
vocab_read=exp/model_slot_tagger__and__hiddenAttention__and__single_cls_CE__with_transformer/data_atis/bidir_True__hid_dim_200_x_1__bs_32__dropout_0.1__optimizer_bertadam__lr_5e-05__mn_1.0__me_6__tes_100__alpha_0.5__bert_bert-base-uncased/vocab
model_read=exp/model_slot_tagger__and__hiddenAttention__and__single_cls_CE__with_transformer/data_atis/bidir_True__hid_dim_200_x_1__bs_32__dropout_0.1__optimizer_bertadam__lr_5e-05__mn_1.0__me_6__tes_100__alpha_0.5__bert_bert-base-uncased/model

max_epoch=10

device=-1
# device=0 means auto-choosing a GPU
# Set deviceId=-1 if you are going to use cpu for training.
experiment_output_path=exp

source ./utils/parse_options.sh

if [[ $tester != true && $tester != True ]]; then
  unset tester
fi

python scripts/slot_tagging_and_intent_detection_with_transformer.py --task_st $task_slot_filling --task_sc $task_intent_detection --dataset $dataset --out_path $outpath --dataroot $dataroot ${testing:+--testing} --read_model $model_read --read_vocab $vocab_read --bidirectional --lr $learning_rate --dropout $dropout_rate --batchSize $batch_size --optim $optimizer --max_norm $max_norm_of_gradient_clip --experiment $experiment_output_path --deviceId $device --max_epoch $max_epoch --hidden_size $lstm_hidden_size --num_layers ${lstm_layers} --tag_emb_size $slot_tag_embedding_size --st_weight ${balance_weight} --pretrained_model_type ${pretrained_model_type} --pretrained_model_name ${pretrained_model_name}
