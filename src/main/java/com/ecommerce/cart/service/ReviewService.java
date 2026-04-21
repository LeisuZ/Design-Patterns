package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Review;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价服务
 * 管理评价信息
 */
public class ReviewService {
    private static final String REVIEW_FILE = "reviews.dat";
    private List<Review> reviews;
    private Logger logger;
    
    public ReviewService() {
        this.logger = Logger.INSTANCE;
        this.reviews = loadReviews();
    }
    
    /**
     * 添加评价
     * @param review 评价对象
     * @return 评价对象
     */
    public Review addReview(Review review) {
        reviews.add(review);
        saveReviews();
        logger.log("Added review for product: " + review.getProductName() + " by " + review.getUserName());
        return review;
    }
    
    /**
     * 添加评价
     * @param productId 商品ID
     * @param productName 商品名称
     * @param userId 用户ID
     * @param userName 用户名称
     * @param rating 评分
     * @param content 评价内容
     * @param orderId 订单ID
     * @return 评价对象
     */
    public Review addReview(String productId, String productName, String userId, String userName, int rating, String content, String orderId) {
        Review review = new Review(productId, productName, userId, userName, rating, content, orderId);
        return addReview(review);
    }
    
    /**
     * 根据商品ID获取评价
     * @param productId 商品ID
     * @return 评价列表
     */
    public List<Review> getReviewsByProductId(String productId) {
        List<Review> productReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (productId.equals(review.getProductId())) {
                productReviews.add(review);
            }
        }
        return productReviews;
    }
    
    /**
     * 根据用户ID获取评价
     * @param userId 用户ID
     * @return 评价列表
     */
    public List<Review> getReviewsByUserId(String userId) {
        List<Review> userReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (userId.equals(review.getUserId())) {
                userReviews.add(review);
            }
        }
        return userReviews;
    }
    
    /**
     * 根据订单ID获取评价
     * @param orderId 订单ID
     * @return 评价列表
     */
    public List<Review> getReviewsByOrderId(String orderId) {
        List<Review> orderReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (orderId.equals(review.getOrderId())) {
                orderReviews.add(review);
            }
        }
        return orderReviews;
    }
    
    /**
     * 根据评价ID获取评价
     * @param id 评价ID
     * @return 评价对象，如果不存在则返回null
     */
    public Review getReviewById(String id) {
        return reviews.stream()
                .filter(review -> id.equals(review.getId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取所有评价
     * @return 评价列表
     */
    public List<Review> getReviews() {
        return reviews;
    }
    
    /**
     * 删除评价
     * @param id 评价ID
     * @return 是否删除成功
     */
    public boolean deleteReview(String id) {
        boolean removed = reviews.removeIf(review -> review.getId().equals(id));
        if (removed) {
            saveReviews();
            logger.log("Deleted review: " + id);
        }
        return removed;
    }
    
    /**
     * 获取商品的平均评分
     * @param productId 商品ID
     * @return 平均评分，如果没有评价则返回0
     */
    public double getAverageRating(String productId) {
        List<Review> productReviews = getReviewsByProductId(productId);
        if (productReviews.isEmpty()) {
            return 0;
        }
        int totalRating = 0;
        for (Review review : productReviews) {
            totalRating += review.getRating();
        }
        return (double) totalRating / productReviews.size();
    }
    
    // 保存评价信息到文件
    private void saveReviews() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REVIEW_FILE))) {
            oos.writeObject(reviews);
            logger.log("Reviews saved to file");
        } catch (IOException e) {
            logger.log("Failed to save reviews: " + e.getMessage());
        }
    }
    
    // 从文件加载评价信息
    private List<Review> loadReviews() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(REVIEW_FILE))) {
            @SuppressWarnings("unchecked")
            List<Review> loadedReviews = (List<Review>) ois.readObject();
            logger.log("Reviews loaded from file");
            return loadedReviews;
        } catch (FileNotFoundException e) {
            logger.log("Reviews file not found, creating new list");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load reviews: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}