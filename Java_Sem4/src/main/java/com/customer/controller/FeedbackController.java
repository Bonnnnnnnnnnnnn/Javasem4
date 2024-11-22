package com.customer.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.customer.repository.FeedbackRepository;
import com.models.Feedback;


@Controller
@RequestMapping("feedback")
public class FeedbackController {
	@Autowired
	FeedbackRepository rep;
   
    @PostMapping("/submit")
    public String submitFeedback(
            @RequestParam(value = "productId", required = true) String productId,
            @RequestParam(value = "orderdetailId", required = true) String orderdetailId,
            @RequestParam(value = "rating", required = true) String rating,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "orderId", required = true) String orderId,
            RedirectAttributes redirectAttributes
    ) {
        try {
        	Integer productIdInt = Integer.parseInt(productId);
            Integer orderdetailIdInt = Integer.parseInt(orderdetailId);
            Integer ratingInt = Integer.parseInt(rating);

            // Tạo đối tượng Feedback
            Feedback fb = new Feedback();
            fb.setProduct_Id(productIdInt);
            fb.setOrderDetail_Id(orderdetailIdInt);
            fb.setRating(ratingInt);
            fb.setComment(comment);
            fb.setStatus(null);
            rep.addFeedback(fb);
            return "redirect:/order/showdetailor?id=" + orderId;

        } catch (NumberFormatException e) {

            return "redirect:/order/showdetailor?id=" + orderId;
        } catch (Exception e) {

            return "redirect:/order/showdetailor?id=" + orderId;
        }
    }
}
