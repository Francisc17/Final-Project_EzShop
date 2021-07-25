import os
import tensorflow as tf
import upload_model as upload

assert tf.__version__.startswith('2')
from tflite_model_maker import image_classifier
from tflite_model_maker.image_classifier import DataLoader


def load_image(path):
    """Loads image."""
    image_raw = tf.io.read_file(path)
    image_tensor = tf.cond(
        tf.image.is_jpeg(image_raw),
        lambda: tf.image.decode_jpeg(image_raw, channels=3),
        lambda: tf.image.decode_png(image_raw, channels=3))
    return image_tensor

data_dir = "./food_photos"
all_image_paths = []
labels = []

directory_contents = os.listdir(data_dir)

for item in directory_contents:
    for root, dirs, files in os.walk(os.path.join(data_dir, item)):
        if len(files) >= 10:
            for file in files:
                if not file.startswith('.'):
                    all_image_paths.append(os.path.join(root, file))
            labels.append(item)

if len(all_image_paths) == 0:
    print("No images to process!")
else:
    all_label_size = len(labels)
    label_to_index = dict(
        (name, index) for index, name in enumerate(labels))
    all_image_labels = [
        label_to_index[os.path.basename(os.path.dirname(path))]
        for path in all_image_paths
    ]

    path_ds = tf.data.Dataset.from_tensor_slices(all_image_paths)

    image_ds = path_ds.map(load_image, num_parallel_calls=tf.data.AUTOTUNE)

    # Loads label.
    label_ds = tf.data.Dataset.from_tensor_slices(tf.cast(all_image_labels, tf.int64))

    # Creates  a dataset if (image, label) pairs.
    image_label_ds = tf.data.Dataset.zip((image_ds, label_ds))

    # Load data
    data = DataLoader(image_label_ds, len(all_image_paths), labels)

    # train_data = data
    validation_data, test_data = data.split(0.5)

    model = image_classifier.create(data, epochs=20, use_augmentation=True, validation_data=validation_data)

    loss, accuracy = model.evaluate(test_data)

    model.export(export_dir='./models/')

    upload.upload_model()
