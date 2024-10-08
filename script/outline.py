import sys
import numpy as np
from PIL import Image

def generate_drop_shadow(image, angle):
    # Convert angle to radians
    angle_rad = np.deg2rad(angle)

    # Calculate the offset for the shadow
    offset_x = int(np.round(np.cos(angle_rad)))
    offset_y = int(np.round(np.sin(angle_rad)))

    # Get image dimensions
    width, height = image.size
    pixels = image.load()

    # Create an output image for the shadow
    shadow_image = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    shadow_pixels = shadow_image.load()

    # Apply shadow generation
    for x in range(width):
        for y in range(height):
            if pixels[x, y][3] > 0:  # Check if the pixel is not transparent
                shadow_x = x + offset_x
                shadow_y = y + offset_y
                if 0 <= shadow_x < width and 0 <= shadow_y < height:
                    shadow_pixels[shadow_x, shadow_y] = (r, g, b, 255)  # White shadow with full opacity

    # For base image
    for x in range(width):
        for y in range(height):
            if pixels[x, y][3] > 0:  # Check if the pixel is not transparent
                shadow_pixels[x, y] = (0, 0, 0, 0)

    return shadow_image

if __name__ == "__main__":
    if len(sys.argv) != 8:
        print("Usage: python script.py <input_image_path> <output_image_path> <angle> <show_base_image> <r> <g> <b>")
        sys.exit(1)

    input_image_path = sys.argv[1]
    output_image_path = sys.argv[2]
    angle = float(sys.argv[3])
    show_base_image = bool(int(sys.argv[4]))
    r = int(sys.argv[5]) * 255
    g = int(sys.argv[6]) * 255
    b = int(sys.argv[7]) * 255

    # Open the input image and split into RGB and alpha channels
    input_image = Image.open(input_image_path).convert("RGBA")

    # Generate the drop shadow at the specified angle
    shadow_image = generate_drop_shadow(input_image, angle)

    # Combine the shadow with the original image
    if show_base_image:
        result_image = Image.alpha_composite(input_image, shadow_image)
        result_image.save(output_image_path, 'PNG')
    else:
        shadow_image.save(output_image_path, 'PNG')

    # Save the resulting image
