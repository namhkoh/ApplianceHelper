#!/usr/Documents/NLP

'''
@Time   : 2020-06-29
@Author : Hyuk Joon Kwon
@Desc   : 
'''


import pandas as pd
import numpy as np 
import re
import random
import copy


def main():
	# # The file that includes the slot and intent information.
	# df = pd.read_excel("Appliance Slot Intent.xlsx",encoding = "ISO-8859-1", sheet_name="Main V5.")
	# # The file that includes the types of values inside that slot.
	# slot_df = pd.read_excel("Appliance Slot Intent.xlsx",encoding = "ISO-8859-1", sheet_name="Slot Description V4.")

	# The file that includes the slot and intent information.
	df = pd.read_excel("Appliance Slot-Intent Manual.xlsx", encoding="ISO-8859-1", sheet_name="Sheet1")
	# The file that includes the types of values inside that slot.
	slot_df = pd.read_excel("Appliance Slot-Intent Manual.xlsx", encoding="ISO-8859-1", sheet_name="Sheet3")

	df['TARGET_QUESTION'] = df['TARGET_QUESTION'].str.lower()
	df["TARGET_QUESTION"] = df['TARGET_QUESTION'].str.replace('[\.\?]', '')
	df['QUESTION_SHOULD_CONTAIN'] = df['QUESTION_SHOULD_CONTAIN'].str.lower()
	slot_df['Slot'] = slot_df['Slot'].str.lower()

	# Get the preprocessed slot_df and a dictionary containing information on the slots. 
	slot_df_, slot_dict = preprocess_slot_df(slot_df)

	# Split the TARGET_QUESTION column and make it into an array of questions
	df.iloc[:, 1] = df.iloc[:, 1].apply(lambda x: x.split('\n'))

	# A Series of arrays representing the respective slots the intents have.
	slots = df.iloc[:, 4].apply(lambda x: x.split(' '))

	# Temporary extract the TARGET_QUESTION column that was processed above.
	target = df.iloc[:, 1]

	# The dictionary where key:Intent value:Question
	target_dic = df.iloc[:, [0, 1]]
	# A dictionary of intents key: int value: intent
	intent_dict = df.QUESTION_LABEL.to_dict()

	# 
	slot_dict_hard_copy = copy.deepcopy(slot_dict)

	slot_dict_boi = get_slot_dict_boi(slot_dict, slot_dict_hard_copy)

	boi_save_train_test(target, slots, slot_dict_boi, intent_dict)

	boi_save_slot_intent(slot_dict_boi, intent_dict)


def preprocess_slot_df(slot_df):
	# Preprocessing slot_df dataframe
	# Slot dataframe trimmed so that the Description is removed. 
	slot_df_ = slot_df.iloc[:, [0, 2]]
	# replace the Example column with an array of the values separated by a comma
	# slot_df_['Examples'] = slot_df_["Examples"].apply(lambda x: x.split(', '))

	# This code is here because of a side effect.
	slot_df_ = slot_df_[slot_df_['Examples'].notnull()].copy()
	value = slot_df_.loc[:, 'Examples'].map(lambda x: x.split(', '))
	slot_df_.loc[:, 'Examples'] = value
	# dictionary of the slots. Key: slot value: possible vallues of the slot. 
	slot_dict = dict(zip(slot_df_.Slot, slot_df_.Examples))

	return slot_df_, slot_dict


def get_slot_dict_boi(slot_dict, slot_dict_boi):
	for key, value in slot_dict.items():
		list_slot = []
		for i in value:
			s = i.split()
			for j in range(len(s)):
				if j == 0:
					s[j] = s[j] + ":B-" + key[1:]
				else:
					s[j] = s[j] + ":I-" + key[1:]
			i = ' '.join(s)
			list_slot.append(i)
		slot_dict_boi[key] = list_slot
	return slot_dict_boi


def boi_save_train_test(target, slots, slot_dict_boi, intent_dict, single = False):
	f = open("./data/appliance/train_test", "w")
	f.close()
	line = ""
	start = 0
	end = len(target)

	with open('./data/appliance/train', 'w') as f_train, open('./data/appliance/test', 'w') as f_test, open('./data/appliance/valid','w') as f_valid:
		# Intent number
		for s in range(start, end):
			# Sentence in the intent
			for j in range(len(target[s])):
				if len(slots[s]) == 2:
					line, _ = boi_two_slots_write(s, j, f_train, f_test, f_valid, slots, slot_dict_boi, target, intent_dict)
				elif len(slots[s]) == 3:
					line, _ = boi_three_slots_write(s, j, f_train, f_test, f_valid,  slots, slot_dict_boi, target, intent_dict)
				elif len(slots[s]) == 1:
					line, _ = boi_one_slot_write(s, j, f_train, f_test, f_valid, slots, slot_dict_boi, target, intent_dict)
					# print("-----------------------------------------------")
					# print("-----------------------------------------------")
		f_train.close()
		f_test.close()
		f_valid.close()




def boi_one_slot_write(s, j, f_train, f_test, f_valid, slots, slot_dict_boi, target, intent_dict):
	p = 0
	# Slot 1
	for i in range(len(slot_dict_boi[slots[s][0]])):
			x = target[s][j]
			x = re.sub(slots[s][0], slot_dict_boi[slots[s][0]][i], x)
			x_boi = re.sub(r'((?:^|(?<= ))[a-zA-Z0-9]+(?= |$))', r'\1:O', x)
			p += 1
			f_train.write(x_boi + " <=> " + intent_dict[s] + "\n")
			# print(x_boi)
	return x, p


def boi_two_slots_write(s, j, f_train, f_test, f_valid, slots, slot_dict_boi, target, intent_dict):
	p = 0
	x_boi = ""
	# Slot 1
	for i in range(len(slot_dict_boi[slots[s][0]])):
		# Slot 2
		for k in range(len(slot_dict_boi[slots[s][1]])):
			x_original = target[s][j]
			x = re.sub(slots[s][0], slot_dict_boi[slots[s][0]][i], x_original)
			x = re.sub(slots[s][1], slot_dict_boi[slots[s][1]][k], x)
			x_boi = re.sub(r'((?:^|(?<= ))[a-zA-Z0-9]+(?= |$))', r'\1:O', x)
			p += 1
			if (j % 4 == 0) or (j % 4 == 1):
				f_train.write(x_boi + " <=> " + intent_dict[s] + "\n")
			elif j % 4 == 2:
				f_test.write(x_boi + " <=> " + intent_dict[s] + "\n")
			else:
				f_valid.write(x_boi + " <=> " + intent_dict[s] + "\n")
	return x, p


def boi_three_slots_write(s, j, f_train, f_test, f_valid, slots, slot_dict_boi, target, intent_dict):
	p = 0
	# Slot 1
	for i in range(len(slot_dict_boi[slots[s][0]])):
		# Slot 2
		for k in range(len(slot_dict_boi[slots[s][1]])):
			# Slot 3
			for l in range(len(slot_dict_boi[slots[s][2]])):
				x_original = target[s][j]
				x = re.sub(slots[s][0], slot_dict_boi[slots[s][0]][i], x_original)
				x = re.sub(slots[s][1], slot_dict_boi[slots[s][1]][k], x)
				x = re.sub(slots[s][2], slot_dict_boi[slots[s][2]][l], x)
				x_boi = re.sub(r'((?:^|(?<= ))[a-zA-Z0-9]+(?= |$))', r'\1:O', x)
				p += 1
				f_train.write(x_boi+" <=> "+ intent_dict[s] + "\n")
				# print(x_boi)
	return x, p


def boi_save_slot_intent(slot_dict_boi, intent_dict):
	slot_array = [] 
	for key,value in slot_dict_boi.items():
		for i in value:
			slot_array = slot_array + i.split()
	slot_array.sort()
	slot_vocab = []
	for i in slot_array:
		slot_vocab.append(i.split(':', 1)[1])
	slot_vocab.sort()
	slot_vocab = list(dict.fromkeys(slot_vocab))
	# for i in slot_vocab:
	#     print(i)
	f = open('./data/appliance/vocab.slot','w')
	for value in slot_vocab:
		f.write(value + "\n")
	f.write("O")
	f.close()

	# Create appliance vocabulary for intents

	f = open('./data/appliance/vocab.intent','w')
	for key,value in intent_dict.items():
		f.write(value + "\n")
	f.close()


if __name__ == "__main__":
	main()

