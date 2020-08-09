using System.Collections;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

/// <summary>
/// This class manages the Oven buttons panels.
/// </summary>
public class OvenPanelManager : MonoBehaviour
{
    /// <summary>
    /// GameObject Panels. 
    /// </summary>
    GameObject[] OvenButton;
    /// <summary>
    /// Dictionary of Microwave Button labels.
    /// </summary>
    public string[] OvenButtons = new string[] { "Bake", "Frozen", "Broil", "Convect Modes", "Keep Warm",
                                                      "AquaLift","Cook Time","Oven Start","Cancel"};
    /// <summary>
    /// Microwave button prefab labels.
    /// </summary>
    public string[] OvenLayers = new string[] { "BakeLayer", "FrozenBakeLayer", "BroilLayer", "ConvectModesLayer",
                                                "KeepWarmLayer","AquaLiftLayer","CookTimeLayer","StartOvenLayer","CancelLayer"};


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
        for (int i = 0; i < OvenButtons.Length; i++)
        {
            obj = MyUtils.FindIncludingInactive(OvenLayers[i]);
            if (valueText == OvenButtons[i].ToLower())
            {
                obj.SetActive(true);
            }
        }
    }
    //public void StoreName()
    //{
    //    theName = inputField.GetComponent<InputField>().text;
    //    textDisplay.GetComponent<Text>().text = theName;
    //    for (int i = 0; i < OvenButtons.Length; i++)
    //    {
    //        obj = MyUtils.FindIncludingInactive(OvenLayers[i]);
    //        if (theName.ToLower() == OvenButtons[i].ToLower())
    //        {
    //            obj.SetActive(true);
    //        }
    //    }
    //}

    //public void SetInactiveO()
    //{
    //    theName = inputField.GetComponent<InputField>().text;
    //    textDisplay.GetComponent<Text>().text = theName;
    //    for (int i = 0; i < OvenButtons.Length; i++)
    //    {
    //        obj = MyUtils.FindIncludingInactive(OvenLayers[i]);
    //        if (theName.ToLower() == OvenButtons[i].ToLower())
    //        {
    //            obj.SetActive(false);
    //        }
    //    }
    //}

    public void SetOvenInactive(string removeLabel)
    {
        valueText = value.GetComponent<Text>().text.ToLower().ToString().Trim();
        valueText = removeLabel.ToLower().Trim();
        for (int i = 0; i < OvenButtons.Length; i++)
        {
            obj = MyUtils.FindIncludingInactive(OvenLayers[i]);
            if (valueText == OvenButtons[i].ToLower())
            {
                obj.SetActive(false);
            }
        }
    }

    public void SendOvenLabel(string label)
    {
        valueText = value.GetComponent<Text>().text.ToLower().ToString().Trim();
        valueText = label.ToLower().Trim();
        for (int i = 0; i < OvenButtons.Length; i++)
        {
            obj = MyUtils.FindIncludingInactive(OvenLayers[i]);
            if (valueText == OvenButtons[i].ToLower())
            {
                obj.SetActive(true);
            }
        }
    }

    /// <summary>
    /// Start function which sets the existing objects in the scene to Inactive on the frame before 
    /// any of the Update methods are called the first time. 
    /// </summary>
    private void Start()
    {
        value = GameObject.FindWithTag("OutputValue");
        OvenButton = GameObject.FindGameObjectsWithTag("OvenButton");
        // Hiding all image targets in the scene.
        foreach (GameObject ob in OvenButton)
        {
            ob.SetActive(false);
        }
    }
}
