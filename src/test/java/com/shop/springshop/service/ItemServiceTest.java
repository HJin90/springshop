package com.shop.springshop.service;

import com.shop.springshop.constant.ItemSellStatus;
import com.shop.springshop.dto.ItemFormDto;
import com.shop.springshop.entity.Item;
import com.shop.springshop.entity.ItemImg;
import com.shop.springshop.repository.ItemImgRepository;
import com.shop.springshop.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws Exception{
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for(int i=0; i<5;i++){
            String path = "C:/shop/item/";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile = new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1,2,3,4});
            multipartFileList.add(multipartFile);
        }

        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception{
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setName("테스트 상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setDetail("테스트 상품 입니다.");
        itemFormDto.setPrice(10000);
        itemFormDto.setStock(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long id = itemService.saveItem(itemFormDto, multipartFileList);
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(id);
        Item item = itemRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        assertEquals(itemFormDto.getName(), item.getName());
        assertEquals(itemFormDto.getItemSellStatus(), item.getStatus());
        assertEquals(itemFormDto.getDetail(), item.getDetail());
        assertEquals(itemFormDto.getPrice(), item.getPrice());
        assertEquals(itemFormDto.getStock(),item.getStock());
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriName());
    }
}
