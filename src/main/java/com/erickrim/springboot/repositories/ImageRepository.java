package com.erickrim.springboot.repositories;

import com.erickrim.springboot.entities.Image;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by krime on 1/9/17.
 */
public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    Image findByName(String name);
}

