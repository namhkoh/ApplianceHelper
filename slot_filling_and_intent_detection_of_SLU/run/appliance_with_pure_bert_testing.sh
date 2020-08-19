#!/bin/bash

source ./path.sh

task_slot_filling=NN # NN, NN_crf
task_intent_detection=CLS # CLS, max, CLS_max
balance_weight=0.5

pretrained_model_type=bert
pretrained_model_name=mrm8488/bert-tiny-finetuned-squadv2 #bert-large-uncased-whole-word-masking #bert-base-uncased

dataroot=data/appliance7
dataset=appliance5
outpath=output

batch_size=32 # 16, 32

optimizer=adam #bertadam, adamw, adam, sgd
learning_rate=5e-5 # 1e-5, 5e-5, 1e-4, 1e-3
max_norm_of_gradient_clip=1 # working for adamw, adam, sgd
dropout_rate=0.1 # 0.1, 0.5

testing=true
vocab_read=exp/test_tiny/vocab
model_read=exp/test_tiny/model

max_epoch=20

device=-1
# device=0 means auto-choosing a GPU
# Set deviceId=-1 if you are going to use cpu for training.
experiment_output_path=exp

source ./utils/parse_options.sh

if [[ $tester != true && $tester != True ]]; then
  unset tester
fi

python scripts/slot_tagging_and_intent_detection_with_pure_transformer_testing.py --task_st $task_slot_filling --task_sc $task_intent_detection --dataset $dataset --out_path $outpath --read_model $model_read --read_vocab $vocab_read --dataroot $dataroot ${testing:+--testing} --lr $learning_rate --dropout $dropout_rate --batchSize $batch_size --optim $optimizer --max_norm $max_norm_of_gradient_clip --experiment $experiment_output_path --deviceId $device --max_epoch $max_epoch --st_weight ${balance_weight} --pretrained_model_type ${pretrained_model_type} --pretrained_model_name ${pretrained_model_name}
