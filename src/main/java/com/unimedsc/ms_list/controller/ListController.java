package com.unimedsc.ms_list.controller;

import com.unimedsc.ms_list.service.GraphListService;
import com.microsoft.graph.models.ListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/list-items")
public class ListController {

    @Autowired
    private GraphListService graphListService;

    @GetMapping
    public List<ListItem> getListItems() {
        return graphListService.getListItems();
    }
}
