using UnityEngine.Events;
using UnityEngine;

//[RequireComponent(typeof(Collider))]
public class ButtonHandler : MonoBehaviour
{
    //public UnityEvent upEvent;
    //public UnityEvent downEvent;

    public void OnMouseDown()
    {
        Debug.Log("Down");
        //downEvent?.Invoke();
    }

    public void OnMouseUp()
    {
        Debug.Log("Up");
        //upEvent?.Invoke();
    }
}