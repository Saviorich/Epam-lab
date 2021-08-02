package com.epam.esm.webservice.service;

import com.epam.esm.webservice.dto.TagDTO;

public interface TagService extends PageableResourceService<TagDTO> {

    TagDTO findMostUsedTagOfUserWithHighestCostOfAllOrders();

}
