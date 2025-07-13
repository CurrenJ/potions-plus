import sys
from PIL import Image

def slice_growth_stages(input_path, output_prefix, stages):
    img = Image.open(input_path).convert("RGBA")
    width, height = img.size

    for stage in range(1, stages + 1):
        # Calculate the visible height for this stage
        visible_height = int(height * stage / stages)
        # Crop the visible part from the bottom up
        crop_box = (0, height - visible_height, width, height)
        plant_part = img.crop(crop_box)
        # Create a transparent image
        stage_img = Image.new("RGBA", (width, height), (0, 0, 0, 0))
        # Paste the plant part at the bottom
        stage_img.paste(plant_part, (0, height - visible_height))
        # Save the stage image
        stage_img.save(f"{output_prefix}_{stage-1}.png")

if __name__ == "__main__":
    # Usage: python script.py input.png output_prefix stages
    if len(sys.argv) != 4:
        print("Usage: python script.py input.png output_prefix stages")
        sys.exit(1)
    slice_growth_stages(sys.argv[1], sys.argv[2], int(sys.argv[3]))