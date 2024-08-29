package com.invoiceissuer.controller;

import com.invoiceissuer.model.DescriptionModel;
import com.invoiceissuer.service.DescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("description")
public class DescriptionController {

    private final DescriptionService descriptionService;

    @Autowired
    public DescriptionController(DescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }

    @PostMapping(path = "addOne")
    public void addDescription(@RequestBody DescriptionModel description) {
        descriptionService.addDescription(description);
    }

    @PostMapping(path = "addMany")
    public void addDescriptionList(@RequestBody List<DescriptionModel> descriptionList) {
        for(DescriptionModel description : descriptionList) {
            descriptionService.addDescription(description);
        }
    }
}
