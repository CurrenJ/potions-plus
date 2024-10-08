import cv2
import numpy as np
from sklearn.cluster import KMeans
from matplotlib import pyplot as plt
import sys
import os

# Load the image
input_image_path = sys.argv[1]
image = cv2.imread(input_image_path)
image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

# Convert the image to HSV color space
hsv_image = cv2.cvtColor(image, cv2.COLOR_RGB2HSV)

# Extract the saturation channel
saturation = hsv_image[:, :, 1]

# Get the x and y coordinates of each pixel
height, width = saturation.shape
x_coords, y_coords = np.meshgrid(np.arange(width), np.arange(height))

# Reshape the saturation data and coordinates for k-means clustering
saturation_reshaped = saturation.reshape(-1, 1)
x_coords_reshaped = x_coords.reshape(-1, 1)
y_coords_reshaped = y_coords.reshape(-1, 1)

# Combine the saturation values with the x and y coordinates
features = np.hstack((saturation_reshaped, x_coords_reshaped, y_coords_reshaped))

# Apply k-means clustering
num_clusters = int(sys.argv[2])
kmeans = KMeans(n_clusters=num_clusters, random_state=0).fit(features)
labels = kmeans.labels_
centers = kmeans.cluster_centers_

# Identify the cluster with the lowest average saturation
num_clusters_to_remove = int(sys.argv[3])
lowest_saturation_clusters = np.argsort(centers[:, 0])[:num_clusters_to_remove]  # 0th column is saturation

# Create a mask to omit pixels belonging to the lowest saturation cluster
mask = ~np.isin(labels, lowest_saturation_clusters).reshape(saturation.shape)

# Apply the mask to the original image
result_image = image.copy()
result_image[~mask] = [0, 0, 0]  # Set omitted pixels to black

# Save the resulting image
output_image_path = os.path.splitext(input_image_path)[0] + '_output.png'
plt.imsave(output_image_path, result_image)