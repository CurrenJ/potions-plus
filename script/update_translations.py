import json
import sys

def generate_translations(java_path, json_path):
    with open(json_path, 'r') as json_file:
        data = json.load(json_file)

    translations = []
    for key, value in data.items():
        variable_name = key.upper().replace('.', '_').replace('-', '_')
        translations.append(f'public static final String {variable_name} = "{key}";')

    class_content = (
        'package grill24.potionsplus.core;\n\n'
        'public class Translations {\n'
        '    // Auto-generated translations\n'
        '    ' + '\n    '.join(translations) + '\n'
        '}\n'
    )

    with open(java_path, 'w') as java_file:
        java_file.write(class_content)

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python generate_translations.py <path_to_Translations.java> <path_to_localization.json>")
        sys.exit(1)

    java_path = sys.argv[1]
    json_path = sys.argv[2]
    generate_translations(java_path, json_path)