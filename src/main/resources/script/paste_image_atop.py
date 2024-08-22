import sys
from PIL import Image, ImageChops

def blend_images_add(image1, image2):
    return ImageChops.add(image1, image2)

def blend_images_subtract(image1, image2):
    return ImageChops.subtract(image1, image2)

def blend_images_screen(image1, image2):
    inv1 = ImageChops.invert(image1)
    inv2 = ImageChops.invert(image2)
    return ImageChops.invert(ImageChops.multiply(inv1, inv2))

def blend_images_overlay(image1, image2):
    return ImageChops.overlay(image1, image2)

def blend_images_darken(image1, image2):
    return ImageChops.darker(image1, image2)

def blend_images_lighten(image1, image2):
    return ImageChops.lighter(image1, image2)

def blend_images_difference(image1, image2):
    return ImageChops.difference(image1, image2)

def blend_images_exclusion(image1, image2):
    return ImageChops.exclusion(image1, image2)

def blend_images_soft_light(image1, image2):
    return ImageChops.soft_light(image1, image2)

def blend_images_hard_light(image1, image2):
    return ImageChops.hard_light(image1, image2)

def blend_default(image1, image2):
    result_image = Image.new('RGBA', image1.size)

    # Paste the two images on top of each other
    result_image.paste(image1, (0, 0), image1)
    result_image.paste(image2, (0, 0), image2)

    return result_image

def apply_opacity(image, opacity):
    image = image.copy()
    alpha = image.split()[3]
    alpha = alpha.point(lambda p:int(p * opacity))
    image.putalpha(alpha)
    return image

if __name__ == "__main__":
    if len(sys.argv) != 6:
        print("Usage: python script.py <image_path1> <image_path2> <output_path> <blend_type> <opacity>")
        sys.exit(1)

    image1 = Image.open(sys.argv[1]).convert("RGBA")
    image2 = Image.open(sys.argv[2]).convert("RGBA")
    blend_type = sys.argv[4]
    opacity = float(sys.argv[5])

    image2 = apply_opacity(image2, opacity)

    # Perform the blending operation
    if blend_type == "add":
        result_image = blend_images_add(image1, image2)
    elif blend_type == "subtract":
        result_image = blend_images_subtract(image1, image2)
    elif blend_type == "screen":
        result_image = blend_images_screen(image1, image2)
    elif blend_type == "overlay":
        result_image = blend_images_overlay(image1, image2)
    elif blend_type == "darken":
        result_image = blend_images_darken(image1, image2)
    elif blend_type == "lighten":
        result_image = blend_images_lighten(image1, image2)
    elif blend_type == "difference":
        result_image = blend_images_difference(image1, image2)
    elif blend_type == "exclusion":
        result_image = blend_images_exclusion(image1, image2)
    elif blend_type == "soft_light":
        result_image = blend_images_soft_light(image1, image2)
    elif blend_type == "hard_light":
        result_image = blend_images_hard_light(image1, image2)
    elif blend_type == "blend":
        result_image = Image.composite(image1, image2, image2)
    else:
        result_image = blend_default(image1, image2)

    result_image.save(sys.argv[3])