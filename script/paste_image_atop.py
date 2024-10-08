import sys
from PIL import Image

def use_mask_to_modulate_value(image, mask, strength):
    # use image 2 as a mask to darken image 1 by modulating hsv values
    width, height = image1.size
    image1hsv = image1.convert("HSV")
    for x in range(width):
        for y in range(height):
            r2, g2, b2, a2 = image2.getpixel((x, y))
            if a2 != 0:
                h1, s1, v1 = image1hsv.getpixel((x, y))
                # darken the value
                v1 = min(int(v1 * strength), 255)
                image1hsv.putpixel((x, y), (h1, s1, v1))

    return image1hsv.convert("RGBA")

def blend_default(image1, image2):
    result_image = Image.new('RGBA', image1.size)

    # Paste the two images on top of each other
    result_image.paste(image1, (0, 0), image1)
    result_image.paste(image2, (0, 0), image2)

    return result_image

if __name__ == "__main__":
    if len(sys.argv) != 6:
        print("Usage: python script.py <image_path1> <image_path2> <output_path> <blend_type> <opacity>")
        sys.exit(1)

    image1 = Image.open(sys.argv[1]).convert("RGBA")
    image2 = Image.open(sys.argv[2]).convert("RGBA")
    blend_type = sys.argv[4]
    opacity = float(sys.argv[5])

    # Perform the blending operation
    if blend_type == "darken":
        result_image = use_mask_to_modulate_value(image1, image2, 1 - opacity)
    elif blend_type == "lighten":
        result_image = use_mask_to_modulate_value(image1, image2, 1 + opacity)
    else:
        result_image = blend_default(image1, image2)

    result_image.save(sys.argv[3])