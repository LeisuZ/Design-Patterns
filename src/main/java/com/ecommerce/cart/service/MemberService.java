package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Member;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 会员服务
 * 管理会员信息
 */
public class MemberService {
    private static final String MEMBER_FILE = "members.dat";
    private List<Member> members;
    private Logger logger;
    
    public MemberService() {
        this.logger = Logger.getInstance();
        this.members = loadMembers();
        if (members.isEmpty()) {
            initDefaultMembers();
        }
    }
    
    private void initDefaultMembers() {
        // 添加默认会员
        addMember("张三", "13800138001", "zhangsan@example.com");
        addMember("李四", "13900139002", "lisi@example.com");
        logger.log("Initialized default members");
    }
    
    /**
     * 添加会员
     * @param name 会员姓名
     * @param phone 会员电话
     * @param email 会员邮箱
     * @return 新创建的会员
     */
    public Member addMember(String name, String phone, String email) {
        String id = "M" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Member member = new Member(id, name, phone, email);
        members.add(member);
        saveMembers();
        logger.log("Added member: " + name);
        return member;
    }
    
    /**
     * 根据ID获取会员
     * @param id 会员ID
     * @return 会员对象，如果不存在则返回null
     */
    public Member getMemberById(String id) {
        return members.stream()
                .filter(member -> member.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据电话获取会员
     * @param phone 会员电话
     * @return 会员对象，如果不存在则返回null
     */
    public Member getMemberByPhone(String phone) {
        return members.stream()
                .filter(member -> member.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取所有会员
     * @return 会员列表
     */
    public List<Member> getMembers() {
        return members;
    }
    
    /**
     * 更新会员信息
     * @param member 会员对象
     */
    public void updateMember(Member member) {
        int index = members.indexOf(members.stream()
                .filter(m -> m.getId().equals(member.getId()))
                .findFirst()
                .orElse(null));
        if (index != -1) {
            members.set(index, member);
            saveMembers();
            logger.log("Updated member: " + member.getName());
        }
    }
    
    /**
     * 删除会员
     * @param id 会员ID
     * @return 是否删除成功
     */
    public boolean deleteMember(String id) {
        boolean removed = members.removeIf(member -> member.getId().equals(id));
        if (removed) {
            saveMembers();
            logger.log("Deleted member: " + id);
        }
        return removed;
    }
    
    /**
     * 会员消费
     * @param memberId 会员ID
     * @param amount 消费金额
     * @return 会员对象，如果不存在则返回null
     */
    public Member spend(String memberId, double amount) {
        Member member = getMemberById(memberId);
        if (member != null) {
            member.spend(amount);
            updateMember(member);
            logger.log("Member " + member.getName() + " spent " + amount + ", points: " + member.getPoints() + ", level: " + member.getLevel().getName());
        }
        return member;
    }
    
    /**
     * 会员兑换积分
     * @param memberId 会员ID
     * @param points 兑换的积分
     * @return 是否兑换成功
     */
    public boolean redeemPoints(String memberId, int points) {
        Member member = getMemberById(memberId);
        if (member != null) {
            boolean success = member.redeemPoints(points);
            if (success) {
                updateMember(member);
                logger.log("Member " + member.getName() + " redeemed " + points + " points");
            }
            return success;
        }
        return false;
    }
    
    // 保存会员信息到文件
    private void saveMembers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MEMBER_FILE))) {
            oos.writeObject(members);
            logger.log("Members saved to file");
        } catch (IOException e) {
            logger.log("Failed to save members: " + e.getMessage());
        }
    }
    
    // 从文件加载会员信息
    private List<Member> loadMembers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MEMBER_FILE))) {
            @SuppressWarnings("unchecked")
            List<Member> loadedMembers = (List<Member>) ois.readObject();
            logger.log("Members loaded from file");
            return loadedMembers;
        } catch (FileNotFoundException e) {
            logger.log("Members file not found, creating new list");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load members: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}