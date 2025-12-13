import numpy as np

BLOCK_SIZE = 2

IMG = np.array([
    [143, 252],
    [5, 111]
])

# 嵌入奇偶校验位到一个块中
def embed_parity_in_a_block(img: np.ndarray, start_pos: tuple, block_size: int = BLOCK_SIZE) -> bool:
    if len(start_pos) != 2:
        return False
    x, y = start_pos
    if x < 0 or x >= img.shape[0] or y < 0 or y >= img.shape[1]:
        return False
    if x + block_size > img.shape[0] or y + block_size > img.shape[1]:
        return False
    block = img[x:x+block_size, y:y+block_size]
    # 计算每一个像素的高七位奇偶性，并嵌入到最低位
    for i in range(block_size):
        for j in range(block_size):
            pixel = block[i, j]
            # 计算高七位的奇偶性
            parity = bin(pixel >> 1).count('1') % 2
            print(f"Parity for pixel ({x+i}, {y+j}) is {parity}")
            # 嵌入到最低位
            block[i, j] = (pixel) & (parity | ~1)
            print(f"Embedded number at ({x+i}, {y+j}) is {block[i, j]}")

    # 替换原图像中的块
    img[x:x+block_size, y:y+block_size] = block
    return True

def verify_parity_in_a_block(img: np.ndarray, start_pos: tuple, block_size: int = BLOCK_SIZE) -> bool:
    if len(start_pos) != 2:
        return False
    x, y = start_pos
    if x < 0 or x >= img.shape[0] or y < 0 or y >= img.shape[1]:
        return False
    if x + block_size > img.shape[0] or y + block_size > img.shape[1]:
        return False
    block = img[x:x+block_size, y:y+block_size]
    # 验证每一个像素的高七位奇偶性是否正确
    for i in range(block_size):
        for j in range(block_size):
            pixel = block[i, j]
            # 计算高七位的奇偶性
            expected_parity = bin(pixel).count('1') % 2
            # 验证图片奇偶校验位是否正确,只需要判断1的个数是否为偶数
            if 0 != expected_parity:
                print(f"Parity check failed at pixel ({x+i}, {y+j}): expected {expected_parity}, got {bin(pixel).count('1') % 2}")
                return False
            
    return True

if __name__ == "__main__":
    embed_parity_in_a_block(IMG, (0, 0))
    print(IMG)
    IMG = np.array([
        [142, 252],
        [129, 111]
    ])
    print(verify_parity_in_a_block(IMG, (0, 0)))
