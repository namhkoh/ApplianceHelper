import pandas as pd
import argparse

def readData(file_name = "Appen\\f1615637.xlsx"):

    data = pd.read_excel(f"{file_name}")

    output = {"_worker_id": [], "edge": [], "template": [], "question_should_contain": [], "question": []}

    for index, row in data.iterrows():
        for i in range(1,4):
            print(row['utterance1'],i,index)
            if '@' in row[f"utterance{i}"]:
                output["_worker_id"].append(row["_worker_id"])
                output["edge"].append(row["edge"])
                output["template"].append(row["template"])
                output["question_should_contain"].append(row["question_should_contain"])
                output["question"].append(row[f"utterance{i}"].replace('"', '').replace("No data available", ""))

    df_pandas = pd.DataFrame.from_dict(output)

    df_pandas.to_excel(f"{file_name.replace('.xlsx', '')}_processed_full.xlsx")
    df_pandas.to_csv(f"{file_name.replace('.xlsx', '')}_processed_full.tsv", sep="\t")


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("--input_file", default="Appen\\f1615637.xlsx", help="Input file in excel format")

    args = parser.parse_args()

    readData(args.input_file)