import sys
from math import ceil, sqrt
from PIL import Image


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("argument error. need : 'message' 'filename'")
        sys.exit()

    string = sys.argv[1]
    filename = sys.argv[2]

    message = ""
    for char in string:
        tmp = bin(ord(char)).replace('b', '')
        message += ('0' if len(tmp) == 7 else '')+tmp

    converter = []
    pixel = []
    for char in message:
        pixel.append(0 if char == "0" else 255)
        if len(pixel) == 3:
            converter.append(tuple(pixel))
            pixel = []
    converter = tuple(converter)

    size = ceil(sqrt(len(converter)))

    img = Image.new('RGB', (size, size), "white")
    img.putdata(converter)
    img = img.resize((size*12, size*12), Image.NEAREST)
    img.show()
    img.save(filename+'.png', 'PNG')
