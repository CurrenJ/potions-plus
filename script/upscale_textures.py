import os
from PIL import Image

def upscale_images(input_folder, output_folder, scale_factor):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.endswith('.png'):
            img_path = os.path.join(input_folder, filename)
            img = Image.open(img_path)
            if img.width != img.height:
                continue
            new_size = (img.width * scale_factor, img.height * scale_factor)
            upscaled_img = img.resize(new_size, Image.NEAREST)
            upscaled_img.save(os.path.join(output_folder, filename))

            print(f'Processing {filename}')

input_folder = '../neoforge/src/main/resources/assets/potionsplus/textures/item/fish'
output_folder = './fish'
scale_factor = 16  # Change this to the desired scale factor

upscale_images(input_folder, output_folder, scale_factor)