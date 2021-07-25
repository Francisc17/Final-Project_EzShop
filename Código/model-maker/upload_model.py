import firebase_admin
from firebase_admin import ml
from firebase_admin import credentials


def upload_model():
    firebase_admin.initialize_app(
        credentials.Certificate('./firebase_credentials/projeto-68ab3-firebase-adminsdk-b6srt-6c741c23c7.json'),
        options={
            'storageBucket': 'projeto-68ab3.appspot.com',
        })

    # See the models id - optional
    face_detectors = ml.list_models().iterate_all()
    print("Models:")
    for model in face_detectors:
        print('{} (ID: {})'.format(model.display_name, model.model_id))

    # Get the existing remote firebase ml model
    model = ml.get_model("16198245")

    # Load a tflite file and upload it to Cloud Storage
    source = ml.TFLiteGCSModelSource.from_tflite_model_file('./models/model.tflite')
    model.model_format = ml.TFLiteFormat(model_source=source)

    # Add the model to your Firebase project and publish it
    updated_model = ml.update_model(model)
    ml.publish_model(updated_model.model_id)
