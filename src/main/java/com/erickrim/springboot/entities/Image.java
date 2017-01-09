package com.erickrim.springboot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by krime on 1/9/17.
 */
@Entity
public class Image {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private Image() {} // reequired by jpa

    public Image(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
