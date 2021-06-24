package com.ltonetwork.client.utils.main;

import static org.junit.Assert.*;

import com.ltonetwork.client.utils.HashUtil;
import org.junit.Test;

public class HashUtilTest {

    @Test
    public void testSHA256() {
        String case1 = "I am SHA256";
        String retCase1 = HashUtil.SHA256(case1);
        assertEquals("40a56524356f19c9e439c11b4a19c27c0e67f2cf079220fb3b51a47e50494a28", retCase1);
    }

    @Test
    public void testKeccak256() {
        String case1 = "I am Keccak256";
        String retCase1 = HashUtil.Keccak256(case1);
        assertEquals("1c476ef188a0991d0eac165736fc1ffbbe7ef5c8c14559d7cb2dabc9f7e4e516", retCase1);

        String case2 = "102a2b4df05f60767406b036053571a115fdc2bfd11597c28d01f0d0064159f7";
        String retCase2 = HashUtil.Keccak256(case2);
        assertEquals("552ea6cb1eb3af91d2a832e64481059129894045c6402d6c91db878d267441c2", retCase2);
    }
}
