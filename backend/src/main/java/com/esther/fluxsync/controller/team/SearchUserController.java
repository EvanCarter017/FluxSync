package com.esther.fluxsync.controller.team;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.service.channel.SearchUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/channel")
class SearchUserController {

    private final SearchUserService searchService;
    public SearchUserController(
            SearchUserService searchService
    ) {
        this.searchService = searchService;
    }

    @GetMapping("/search/{cid}")
    public ResponseEntity<ApiResponse<?>> search(@PathVariable String cid) {

        return new ResponseEntity<>(ApiResponse.success(searchService.search(cid)), HttpStatus.OK);

    }

}
