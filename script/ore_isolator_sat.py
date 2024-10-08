import cv2
import numpy as np
from sklearn.cluster import KMeans
from matplotlib import pyplot as plt
import sys
import os

# 0 1 1 6 3

# Load the image
input_image_path = sys.argv[1]
image = cv2.imread(input_image_path)
image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

# Convert the image to HSV color space
hsv_image = cv2.cvtColor(image, cv2.COLOR_RGB2HSV)

# Parse command-line arguments to determine which features are enabled
enable_hue = bool(int(sys.argv[2]))
enable_saturation = bool(int(sys.argv[3]))
enable_value = bool(int(sys.argv[4]))
num_clusters = int(sys.argv[5])
num_clusters_to_remove = int(sys.argv[6])

# Extract the enabled features
features = []
if enable_hue:
    hue = hsv_image[:, :, 0].reshape(-1, 1)
    features.append(hue)
if enable_saturation:
    saturation = hsv_image[:, :, 1].reshape(-1, 1)
    features.append(saturation)
if enable_value:
    value = hsv_image[:, :, 2].reshape(-1, 1)
    features.append(value)

# Combine the enabled features
features = np.hstack(features)

# Apply k-means clustering
kmeans = KMeans(n_clusters=num_clusters, random_state=0).fit(features)
labels = kmeans.labels_
centers = kmeans.cluster_centers_

# Identify the clusters with the lowest average saturation
if enable_saturation:
    if num_clusters_to_remove > 0:
        lowest_saturation_clusters = np.argsort(centers[:, features.shape[1] - 1])[:num_clusters_to_remove]
    else:
        lowest_saturation_clusters = np.argsort(centers[:, features.shape[1] - 1])[num_clusters_to_remove:]
else:
    if num_clusters_to_remove > 0:
        lowest_saturation_clusters = np.argsort(centers[:, 0])[:num_clusters_to_remove]  # Default to first column if saturation is not enabled
    else:
        lowest_saturation_clusters = np.argsort(centers[:, 0])[num_clusters_to_remove:]  # Default to first column if saturation is not enabled

# Create a mask to omit pixels belonging to the lowest saturation clusters
mask = ~np.isin(labels, lowest_saturation_clusters).reshape(hsv_image.shape[:2])

# Apply the mask to the original image
result_image = cv2.cvtColor(image, cv2.COLOR_RGB2RGBA)
result_image[~mask] = [0, 0, 0, 0]  # Set omitted pixels to black

# Save the resulting image
output_image_path = os.path.splitext(input_image_path)[0] + '_isolated.png'
plt.imsave(output_image_path, result_image)