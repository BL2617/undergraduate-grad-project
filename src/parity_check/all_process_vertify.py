import numpy as np
import hashlib

BLOCK_SIZE = 2

IMG = np.array([
    [143, 252],
    [5, 111]
])

def judge_block_legal(img: np.array, start_pos: tuple, block_size: int = BLOCK_SIZE) -> bool:
    if len(start_pos) != 2:
        return False
    x, y = start_pos
    if x < 0 or x >= img.shape[0] or y < 0 or y >= img.shape[1]:
        return False
    if x + block_size > img.shape[0] or y + block_size > img.shape[1]:
        return False
    return True

# 嵌入奇偶校验位到一个块中
def embed_parity_in_a_block(img: np.ndarray, start_pos: tuple, block_size: int = BLOCK_SIZE) -> bool:
    if not judge_block_legal(img, start_pos, block_size):
        return False
    
    x, y = start_pos
    block = img[x:x+block_size, y:y+block_size]
    # 计算每一个像素的高七位奇偶性，并嵌入到最低位
    for i in range(block_size):
        for j in range(block_size):
            pixel = block[i, j]
            # 计算高七位的奇偶性
            parity = bin(pixel >> 1).count('1') % 2
            # print(f"Parity for pixel ({x+i}, {y+j}) is {parity}")
            # 嵌入到最低位
            block[i, j] = (pixel) & (parity | ~1)
            # print(f"Embedded number at ({x+i}, {y+j}) is {block[i, j]}")

    # 替换原图像中的块
    img[x:x+block_size, y:y+block_size] = block
    return True

# 使用用户私钥对图像块最低位进行异或以加密
def hash_key_encrypt_parity_in_a_block(img: np.array, start_pos: tuple, block_size: int = BLOCK_SIZE, user_key: str = "", useEncrypt: bool = True, hash_type: str = "sha256") -> bool:
    if not judge_block_legal(img, start_pos, block_size):
        return False
    
    block = img[start_pos[0]:start_pos[0]+block_size, start_pos[1]:start_pos[1]+block_size]

    key_hash = hashlib.__dict__[hash_type](user_key.encode()).hexdigest()
    hash_bin = ''.join([bin(int(c, 16))[2:].zfill(4) for c in key_hash])
    index_of_hash_bin = 0
    # 对每个像素的最低位进行异或操作
    for i in range(block_size):
        for j in range(block_size):
            block[i, j] ^= int(hash_bin[index_of_hash_bin], 2)
            index_of_hash_bin = (index_of_hash_bin + 1) % len(hash_bin)
    # 替换原图像中的块
    img[start_pos[0]:start_pos[0]+block_size, start_pos[1]:start_pos[1]+block_size] = block
    return True

def hash_key_decrypt_parity_in_a_block(img: np.array, start_pos: tuple, block_size: int = BLOCK_SIZE, user_key: str = "", hash_type: str = "sha256") -> bool:
    return hash_key_encrypt_parity_in_a_block(img, start_pos, block_size, user_key, False, hash_type)   

def verify_parity_in_a_block(img: np.ndarray, start_pos: tuple, block_size: int = BLOCK_SIZE) -> bool:
    if not judge_block_legal(img, start_pos, block_size):
        return False
    
    x, y = start_pos
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

def print_last_bit_of_each_pixel_in_block(img: np.array, start_pos: tuple, block_size: int = BLOCK_SIZE) -> None:
    if not judge_block_legal(img, start_pos, block_size):
        return
    
    x, y = start_pos
    block = img[x:x+block_size, y:y+block_size]
    # 打印每个像素的最低位
    for i in range(block_size):
        for j in range(block_size):
            pixel = block[i, j]
            print(f"{pixel & 1}", end="")
    print("\n")

def print_bin_of_key_hash(user_key: str, hash_type: str = "sha256") -> None:
    key_hash = hashlib.__dict__[hash_type](user_key.encode()).hexdigest()
    hash_bin = ''.join([bin(int(c, 16))[2:].zfill(4) for c in key_hash])
    print(f"Hash bin of key {user_key} is {hash_bin}")

if __name__ == "__main__":
    key = "123456989087aeijg98"
    embed_parity_in_a_block(IMG, (0, 0))
    print("After embed parity:")
    print(IMG)
    print("Last bit of each pixel in block:")
    print_last_bit_of_each_pixel_in_block(IMG, (0, 0))
    hash_key_encrypt_parity_in_a_block(IMG, (0, 0), user_key=key)
    print("After encrypt parity:")
    print(f"Hash of key {key}:")
    print_bin_of_key_hash(key)
    print("Last bit of each pixel in block after encrypt:")
    print_last_bit_of_each_pixel_in_block(IMG, (0, 0))
    print("The parity check result is:", verify_parity_in_a_block(IMG, (0, 0)))
    hash_key_decrypt_parity_in_a_block(IMG, (0, 0), user_key=key)
    print("After decrypt parity:")
    print("Last bit of each pixel in block after decrypt:")
    print_last_bit_of_each_pixel_in_block(IMG, (0, 0))

    print("The parity check result after decrypt is:", verify_parity_in_a_block(IMG, (0, 0)))
