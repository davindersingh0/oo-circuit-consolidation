package com.oneops.circuitconsolidation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OOCircuitConsolidationRestController {

  private final Logger log = LoggerFactory.getLogger(getClass());


  @RequestMapping(value = "/phase/design/platform/{platformID}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> performCircuitConsolidation(@PathVariable long platformID) {

    try {
      return new ResponseEntity<String>("I am working!", HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }



  }


}
