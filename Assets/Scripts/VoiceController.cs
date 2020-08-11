using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Android;
using System;
using TMPro;

namespace FrostweepGames.Plugins.GoogleCloud.SpeechRecognition.Examples
{
    public class VoiceController : MonoBehaviour
    {

        [SerializeField]
        private GCSpeechRecognition _speechRecognition;

        //[SerializeField]
        //private Button //_startRecordButton,
                       //_stopRecordButton,
                       //_recognizeButton;

        [SerializeField]
        public Text _resultText;
        public Text _resultHandler;

        //[SerializeField]
        //private Toggle _recognizeDirectlyToggle;

        [SerializeField]
        private Dropdown _languageDropdown,
                         _microphoneDevicesDropdown;

        [SerializeField]
        private Image _voiceLevelImage;



        void Start()
        {
            CheckPermission();

            _speechRecognition = GCSpeechRecognition.Instance;
            _speechRecognition.RecognizeSuccessEvent += RecognizeSuccessEventHandler;
            _speechRecognition.RecognizeFailedEvent += RecognizeFailedEventHandler;

            _speechRecognition.FinishedRecordEvent += FinishedRecordEventHandler;
            _speechRecognition.StartedRecordEvent += StartedRecordEventHandler;
            _speechRecognition.RecordFailedEvent += RecordFailedEventHandler;

            _speechRecognition.BeginTalkigEvent += BeginTalkigEventHandler;
            _speechRecognition.EndTalkigEvent += EndTalkigEventHandler;

            //_startRecordButton.onClick.AddListener(StartRecordButtonOnClickHandler);
            //_stopRecordButton.onClick.AddListener(StopRecordButtonOnClickHandler);
            //_recognizeButton.onClick.AddListener(RecognizeButtonOnClickHandler);

            _microphoneDevicesDropdown.onValueChanged.AddListener(MicrophoneDevicesDropdownOnValueChangedEventHandler);

            //_startRecordButton.interactable = true;
            //_stopRecordButton.interactable = false;
            //_speechRecognitionState.color = Color.yellow;

            _languageDropdown.ClearOptions();

            for (int i = 0; i < Enum.GetNames(typeof(Enumerators.LanguageCode)).Length; i++)
            {
                _languageDropdown.options.Add(new Dropdown.OptionData(((Enumerators.LanguageCode)i).Parse()));
            }

            _languageDropdown.value = _languageDropdown.options.IndexOf(_languageDropdown.options.Find(x => x.text == Enumerators.LanguageCode.en_GB.Parse()));

            RefreshMicsButtonOnClickHandler();
        }

        private void OnDestroy()
        {
            _speechRecognition.RecognizeSuccessEvent -= RecognizeSuccessEventHandler;
            _speechRecognition.RecognizeFailedEvent -= RecognizeFailedEventHandler;

            _speechRecognition.FinishedRecordEvent -= FinishedRecordEventHandler;
            _speechRecognition.StartedRecordEvent -= StartedRecordEventHandler;
            _speechRecognition.RecordFailedEvent -= RecordFailedEventHandler;

            _speechRecognition.EndTalkigEvent -= EndTalkigEventHandler;
        }

        private void Update()
        {
            if (_speechRecognition.IsRecording)
            {
                if (_speechRecognition.GetMaxFrame() > 0)
                {
                    float max = (float)_speechRecognition.configs[_speechRecognition.currentConfigIndex].voiceDetectionThreshold;
                    float current = _speechRecognition.GetLastFrame() / max;

                    if (current >= 1f)
                    {
                        _voiceLevelImage.fillAmount = Mathf.Lerp(_voiceLevelImage.fillAmount, Mathf.Clamp(current / 2f, 0, 1f), 30 * Time.deltaTime);
                    }
                    else
                    {
                        _voiceLevelImage.fillAmount = Mathf.Lerp(_voiceLevelImage.fillAmount, Mathf.Clamp(current / 2f, 0, 0.5f), 30 * Time.deltaTime);
                    }

                    _voiceLevelImage.color = current >= 1f ? Color.green : Color.red;
                }
            }
            else
            {
                _voiceLevelImage.fillAmount = 0f;
            }
        }

        void CheckPermission()
        {
#if UNITY_ANDROID

            if (!Permission.HasUserAuthorizedPermission(Permission.Microphone))
            {
                Permission.RequestUserPermission(Permission.Microphone);
            }

#endif
        }

        private void RefreshMicsButtonOnClickHandler()
        {
            _speechRecognition.RequestMicrophonePermission(null);

            _microphoneDevicesDropdown.ClearOptions();

            for (int i = 0; i < _speechRecognition.GetMicrophoneDevices().Length; i++)
            {
                _microphoneDevicesDropdown.options.Add(new Dropdown.OptionData(_speechRecognition.GetMicrophoneDevices()[i]));
            }

            //smart fix of dropdowns
            _microphoneDevicesDropdown.value = 1;
            _microphoneDevicesDropdown.value = 0;
        }

        private void MicrophoneDevicesDropdownOnValueChangedEventHandler(int value)
        {
            if (!_speechRecognition.HasConnectedMicrophoneDevices())
                return;
            _speechRecognition.SetMicrophoneDevice(_speechRecognition.GetMicrophoneDevices()[value]);
        }

        public void StartRecordButtonOnClickHandler()
        {
            //_startRecordButton.interactable = false;
            //_stopRecordButton.interactable = true;
            _resultText.text = string.Empty;
            _resultHandler.text = string.Empty;

            _speechRecognition.StartRecord(false);
        }

        public void StopRecordButtonOnClickHandler()
        {
            //_stopRecordButton.interactable = false;
            //_startRecordButton.interactable = true;

            _speechRecognition.StopRecord();
        }

        private void RecognizeButtonOnClickHandler()
        {
            if (_speechRecognition.LastRecordedClip == null)
            {
                _resultText.text = "No Record found";
                return;
            }

            FinishedRecordEventHandler(_speechRecognition.LastRecordedClip, _speechRecognition.LastRecordedRaw);
        }

        private void StartedRecordEventHandler()
        {
            //_speechRecognitionState.color = Color.red;
            _resultText.text = "Listening...";
        }

        private void RecordFailedEventHandler()
        {
            //_speechRecognitionState.color = Color.yellow;
            _resultText.text = "Start record Failed. Please check microphone device and try again.";

            //_stopRecordButton.interactable = false;
            //_startRecordButton.interactable = true;
        }

        private void BeginTalkigEventHandler()
        {
            _resultText.text = "Talk Began";
        }

        private void EndTalkigEventHandler(AudioClip clip, float[] raw)
        {
            _resultText.text += "\nTalk Ended";

            FinishedRecordEventHandler(clip, raw);
        }

        private void FinishedRecordEventHandler(AudioClip clip, float[] raw)
        {
            //if (_startRecordButton.interactable)
            //{
            //_speechRecognitionState.color = Color.yellow;
            //}

            //if (clip == null || !_recognizeDirectlyToggle.isOn)
            if (clip == null)
                return;

            RecognitionConfig config = RecognitionConfig.GetDefault();
            config.languageCode = ((Enumerators.LanguageCode)_languageDropdown.value).Parse();
            config.speechContexts = new SpeechContext[]
            {
                //new SpeechContext()
                //{
                //    phrases = _contextPhrasesInputField.text.Replace(" ", string.Empty).Split(',')
                //}
            };
            config.audioChannelCount = clip.channels;
            // configure other parameters of the config if need

            GeneralRecognitionRequest recognitionRequest = new GeneralRecognitionRequest()
            {
                audio = new RecognitionAudioContent()
                {
                    content = raw.ToBase64()
                },
                //audio = new RecognitionAudioUri() // for Google Cloud Storage object
                //{
                //	uri = "gs://bucketName/object_name"
                //},
                config = config
            };
            _speechRecognition.Recognize(recognitionRequest);
        }

        private void RecognizeFailedEventHandler(string error)
        {
            _resultText.text = "Recognize Failed: " + error;
        }

        private void RecognizeSuccessEventHandler(RecognitionResponse recognitionResponse)
        {
            _resultText.text = "Recognize Success.";
            InsertRecognitionResponseInfo(recognitionResponse);
        }

        private void InsertRecognitionResponseInfo(RecognitionResponse recognitionResponse)
        {
            if (recognitionResponse == null || recognitionResponse.results.Length == 0)
            {
                _resultText.text = "\nWords not detected.";
                return;
            }

            _resultText.text += "\n" + recognitionResponse.results[0].alternatives[0].transcript;
            _resultHandler.text += "\n" + recognitionResponse.results[0].alternatives[0].transcript;
            Debug.Log("NL QUERY : " + _resultText.text);
            Debug.Log("Result Handler :" + _resultHandler.text);

            var words = recognitionResponse.results[0].alternatives[0].words;

            if (words != null)
            {
                string times = string.Empty;

                foreach (var item in recognitionResponse.results[0].alternatives[0].words)
                {
                    times += item.word + "start: " + item.startTime + "; end: " + item.endTime + "\n";
                }

                _resultText.text += "\n" + times;
            }

            string other = "\nDetected alternatives: ";

            foreach (var result in recognitionResponse.results)
            {
                foreach (var alternative in result.alternatives)
                {
                    if (recognitionResponse.results[0].alternatives[0] != alternative)
                    {
                        other += alternative.transcript + ", ";
                    }
                }
            }

            _resultText.text += other;
        }

    }
}