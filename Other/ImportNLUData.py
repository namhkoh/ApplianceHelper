import pandas as pd
import argparse

def readData(file_name = "Appen\\appliance_utterances_test_l3.xlsx"):

    data = pd.read_excel(file_name)

    output = {"_worker_id": [], "edge": [], "template": [], "question_should_contain": [], "question": []}

    for index, row in data.iterrows():
        for i in range(1,4):
            if '@' in row[f"utterance{i}"]:
                output["_worker_id"].append(row["_worker_id"])
                output["edge"].append(row["edge"])
                output["template"].append(row["template"])
                output["question_should_contain"].append(row["question_should_contain"])
                output["question"].append(row[f"utterance{i}"].replace('"', '').replace("No data available", ""))

    df_pandas = pd.DataFrame.from_dict(output)

    df_pandas.to_excel(f"{file_name.replace('.xlsx', '')}_processed.xlsx")
    df_pandas.to_csv(f"{file_name.replace('.xlsx', '')}_processed.tsv", sep="\t")


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("--input_file", default="Appen\\appliance_utterances_test_l3.xlsx", help="Input file in excel format")

    args = parser.parse_args()

    readData(args.input_file)