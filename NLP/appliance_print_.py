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


def one_slot(s, j, slot_dict, slots, target):
    p = 0
    # Slot 1
    for i in range(len(slot_dict[slots[s][0]])):
            x = target[s][j]
            x = re.sub(slots[s][0], slot_dict[slots[s][0]][i], x)
            p += 1
            print(x)
    return p


def two_slots(s, j, slot_dict, slots, target):
    p = 0
    for i in range(len(slot_dict[slots[s][0]])):  # Slot 1
        for k in range(len(slot_dict[slots[s][1]])):  # Slot 2
            x = target[s][j]
            x = re.sub(slots[s][0], slot_dict[slots[s][0]][i], x)
            x = re.sub(slots[s][1], slot_dict[slots[s][1]][k], x)
            p += 1
            print(x)
    return p


def three_slots(s, j, slot_dict, slots, target):
    p = 0
    for i in range(len(slot_dict[slots[s][0]])):  # Slot 1
        for k in range(len(slot_dict[slots[s][1]])):  # Slot 2
            for l in range(len(slot_dict[slots[s][2]])):  # Slot 3
                x = target[s][j]
                x = re.sub(slots[s][0], slot_dict[slots[s][0]][i], x)
                x = re.sub(slots[s][1], slot_dict[slots[s][1]][k], x)
                x = re.sub(slots[s][2], slot_dict[slots[s][2]][l], x)
                p += 1
                print(x)
    return p


def print_all(intent_number, slot_dict, slots, target, intent_dict, single=False):
    i = 0
    start = 0
    end = intent_number
    if single:
        start = intent_number - 1
        end = intent_number
    # Intent number
    for s in range(start, end):
        print("-----------------------------------------------")
        print("Intent:", intent_dict[s], "|", "number of slots:", len(slots[s]))
        for j in range(len(target[s])):  # Sentence in the intent
            if len(slots[s]) == 2:
                # print("two slots")
                i += two_slots(s, j, slot_dict, slots, target)
            elif len(slots[s]) == 3:
                # print("three slots")
                i += three_slots(s, j, slot_dict, slots, target)
            elif len(slots[s]) == 1:
                # print("one slot")
                i += one_slot(s, j, slot_dict, slots, target)
            print("-----------------------------------------------")
    print("-----------------------------------------------")

    print(i)


def main():
    # The file that includes the slot and intent information.
    df = pd.read_excel("Appliance Slot Intent.xlsx", encoding="ISO-8859-1", sheet_name="Main V4.")
    # The file that includes the types of values inside that slot.
    slot_df = pd.read_excel("Appliance Slot Intent.xlsx", encoding="ISO-8859-1", sheet_name="Slot Description V4.")

    # Slot dataframe trimmed so that the Description is removed.
    slot_df_ = slot_df.iloc[:, [0, 2]]

    # replace the Example column with an array of the values separated by a comma
    slot_df_ = slot_df_[slot_df_['Examples'].notnull()].copy()
    slot_df_['Examples'] = slot_df_["Examples"].apply(lambda x: x.split(', '))

    # dictionary of the slots. Key: slot value: possible vallues of the slot.
    slot_dict = dict(zip(slot_df_.Slot, slot_df_.Examples))

    # Split the TARGET_QUESTION column and make it into an array of questions
    df.iloc[:, 1] = df.iloc[:, 1].apply(lambda x: x.split('\n'))

    # Temporary extract the TARGET_QUESTION column that was processed above.
    target = df.iloc[:, 1]

    # creating the slots for each dictionary
    slots = df.iloc[:, 4].apply(lambda x: x.split(' '))

    target_dic = df.iloc[:, [0, 1]]
    intent_dict = df.QUESTION_LABEL.to_dict()

    print_all(len(target), slot_dict, slots, target, intent_dict, False)


if __name__ == "__main__":
    main()
