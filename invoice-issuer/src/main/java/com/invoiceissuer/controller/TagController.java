package com.invoiceissuer.controller;

import com.invoiceissuer.model.TagModel;
import com.invoiceissuer.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tag")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping(path = "addOne")
    public void addDescription(@RequestBody TagModel tag) {
        tagService.addTag(tag);
    }

    @PostMapping(path = "addMany")
    public void addDescriptionList(@RequestBody List<TagModel> tagList) {
        for(TagModel tag : tagList) {
            tagService.addTag(tag);
        }
    }
}
