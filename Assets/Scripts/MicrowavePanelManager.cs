using FrostweepGames.Plugins.GoogleCloud.SpeechRecognition.Examples;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

/// <summary>
/// This class manages the Microwave buttons panels.
/// </summary>
public class MicrowavePanelManager : MonoBehaviour
{
    /// <summary>
    /// GameObject Panels. 
    /// </summary>
    GameObject[] MicrowaveButton;
    /// <summary>
    /// Dictionary of Microwave Button labels.
    /// </summary>
    public string[] MicrowaveButtons = new string[] { "Popcorn", "Potato", "Pizza", "Cook", "Reheat",
                                                      "Melt","Cook Power", "Defrost","Start","Clock"};
    /// <summary>
    /// Microwave button prefab labels.
    /// </summary>
    public string[] MicrowaveLayers = new string[] { "PopcornLayer", "PotatoLayer", "PizzaLayer", "CookLayer", "ReheatLayer",
                                                      "SoftenMeltLayer","CookPowerLayer", "DefrostLayer","StartLayer","ClockLayer"};


    //Dictionary<string, GameObject> LabelDict = new Dictionary<string, GameObject>();

    /// <summary>
    /// Singluar GameObject.
    /// </summary>
    GameObject obj;

    GameObject value;
    string valueText;

    //public string theName;
    //public GameObject inputField;
    //public GameObject textDisplay;


    /// <summary>
    /// Compares user input label with button dictionary to display panel in AR. 
    /// </summary>
    public void StoreLabel()
    {
        valueText = value.GetComponent<Text>().text.ToLower().ToString().Trim();
        for (int i = 0; i < MicrowaveButtons.Length; i++)
        {
            obj = MyUtils.FindIncludingInactive(MicrowaveLayers[i]);
            if (valueText == MicrowaveButtons[i].ToLower())
            {
                obj.SetActive(true);
            }
        }
    }

    //public void StoreName()
    //{
    //    theName = inputField.GetComponent<InputField>().text;
    //    textDisplay.GetComponent<Text>().text = theName;
    //    for (int i = 0; i < MicrowaveButtons.Length; i++)
    //    {
    //        obj = MyUtils.FindIncludingInactive(MicrowaveLayers[i]);
    //        if (theName.ToLower() == MicrowaveButtons[i].ToLower())
    //        {
    //            obj.SetActive(true);
    //        }
    //    }
    //}

    //public void SetInactiveM()
    //{
    //    theName = inputField.GetComponent<InputField>().text;
    //    textDisplay.GetComponent<Text>().text = theName;
    //    for (int i = 0; i < MicrowaveButtons.Length; i++)
    //    {
    //        obj = MyUtils.FindIncludingInactive(MicrowaveLayers[i]);
    //        if (theName.ToLower() == MicrowaveButtons[i].ToLower())
    //        {
    //            obj.SetActive(false);
    //        }
    //    }
    //}

    public void SetMicrowaveInactive(string removeLabel)
    {
        valueText = value.GetComponent<Text>().text.ToLower().ToString().Trim();
        valueText = removeLabel.ToLower().Trim();
        for (int i = 0; i < MicrowaveButtons.Length; i++)
        {
            obj = MyUtils.FindIncludingInactive(MicrowaveLayers[i]);
            if (valueText == MicrowaveButtons[i].ToLower())
            {
                obj.SetActive(false);
            }
        }
    }


    public void SendMicrowaveLabel(string label)
    {
        valueText = value.GetComponent<Text>().text.ToLower().ToString().Trim();
        valueText = label.ToLower().Trim();
        Debug.Log(valueText);
        //valueText = "pizza";
        for (int i = 0; i < MicrowaveButtons.Length; i++)
        {
            obj = MyUtils.FindIncludingInactive(MicrowaveLayers[i]);
            if (valueText == MicrowaveButtons[i].ToLower())
            {
                obj.SetActive(true);
            }
        }
    }

    //public void OnButtonStoreLabel() {
    //    StartCoroutine(CheckInputLabel());
    //}

    //IEnumerator CheckInputLabel() {
    //    valueText = value.GetComponent<Text>().text.ToLower().ToString().Trim();
    //    for (int i = 0; i < MicrowaveButtons.Length; i++)
    //    {
    //        obj = MyUtils.FindIncludingInactive(MicrowaveLayers[i]);
    //        if (valueText == MicrowaveButtons[i].ToLower())
    //        {
    //            obj.SetActive(true);
    //        }
    //    }

    //    yield return true;

    //    if (valueText == null) {
    //        yield break;
    //    }
    //}

    /// <summary>
    /// Start function which sets the existing objects in the scene to Inactive on the frame before 
    /// any of the Update methods are called the first time. 
    /// </summary>
    private void Start()
    {
        value = GameObject.FindWithTag("OutputValue");
        MicrowaveButton = GameObject.FindGameObjectsWithTag("MicrowaveButton");
        // Hiding all image targets in the scene.
        foreach (GameObject mb in MicrowaveButton)
        {
            mb.SetActive(false);
        }
    }
}
