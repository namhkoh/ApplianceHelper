using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class PanelManager : MonoBehaviour
{

    private List<string> LabelString = new List<string>();

    public string label;
    public GameObject inputField;
    public GameObject textDisplay;

    GameObject[] MicrowaveButton;
    public string[] MicrowaveButtons = new string[] { "Popcorn", "Potato", "Pizza", "Cook", "Reheat",
                                                      "Soften","Cook Time","Cook Power", "Defrost"};
    public string[] MicrowaveLayers = new string[] { "PopcornLayer", "PotatoLayer", "PizzaLayer", "CookLayer", "ReheatLayer",
                                                      "SoftenMeltLayer","CookTimeLayer","CookPowerLayer", "DefrostLayer"};


    Dictionary<string, GameObject[]> LabelDict = new Dictionary<string, GameObject[]>();
    GameObject obj;

    public void StoreLabel()
    {
        label = inputField.GetComponent<Text>().text;
        textDisplay.GetComponent<Text>().text = label;
        for (int i = 0; i < MicrowaveButtons.Length; i++)
        {
            obj = MyUtils.FindIncludingInactive(MicrowaveLayers[i]);
            if (label == MicrowaveButtons[i])
            {
                obj.SetActive(true);
            }
        }
    }

    private void Start()
    {
        MicrowaveButton = GameObject.FindGameObjectsWithTag("MicrowaveButton");
        //LabelDict.Add("Microwave", MicrowaveButton);
        //Debug.Log(LabelDict["Microwave"]);
        // Hiding all image targets in the scene.
        foreach (GameObject mb in MicrowaveButton)
        {
            mb.SetActive(false);
        }
    }
}
