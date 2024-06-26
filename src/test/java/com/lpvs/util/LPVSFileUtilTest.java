/**
 * Copyright (c) 2022, Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */
package com.lpvs.util;

import com.lpvs.entity.LPVSQueue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHPullRequestFileDetail;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class LPVSFileUtilTest {

    @Test
    public void testSaveGithubDiffs() {
        GHPullRequestFileDetail detail = new GHPullRequestFileDetail();
        LPVSQueue webhookConfig = new LPVSQueue();
        webhookConfig.setHeadCommitSHA("aaaa");
        webhookConfig.setRepositoryUrl("http://test.com/test/test");
        ReflectionTestUtils.setField(detail, "filename", "I_am_a_file");
        LPVSFileUtil.saveGithubDiffs(
                new ArrayList<GHPullRequestFileDetail>() {
                    {
                        add(detail);
                    }
                },
                webhookConfig);
        ReflectionTestUtils.setField(detail, "patch", "+ a\n- b\n@@ -8,7 +8,6 @@\n c");
        Assertions.assertFalse(
                LPVSFileUtil.saveGithubDiffs(
                                new ArrayList<GHPullRequestFileDetail>() {
                                    {
                                        add(detail);
                                    }
                                },
                                webhookConfig)
                        .contains("Projects//aaaa"));
    }

    @Test
    public void testSaveGithubDiffsFileNameWithSlash() {
        GHPullRequestFileDetail detail = new GHPullRequestFileDetail();
        LPVSQueue webhookConfig = new LPVSQueue();
        webhookConfig.setHeadCommitSHA("aaaa");
        webhookConfig.setRepositoryUrl("http://test.com/test/test");
        ReflectionTestUtils.setField(detail, "filename", "dir/I_am_a_file");
        LPVSFileUtil.saveGithubDiffs(
                new ArrayList<GHPullRequestFileDetail>() {
                    {
                        add(detail);
                    }
                },
                webhookConfig);

        ReflectionTestUtils.setField(detail, "patch", "+ a\n- b\n@@ -8,7 +8,6 @@\n c");
        Assertions.assertFalse(
                LPVSFileUtil.saveGithubDiffs(
                                new ArrayList<GHPullRequestFileDetail>() {
                                    {
                                        add(detail);
                                    }
                                },
                                webhookConfig)
                        .contains("Projects//aaaa"));
    }

    @Test
    public void testSaveGithubDiffsEmptyPatchLines() {
        GHPullRequestFileDetail detail = new GHPullRequestFileDetail();
        LPVSQueue webhookConfig = new LPVSQueue();
        webhookConfig.setHeadCommitSHA("aaaa");
        webhookConfig.setRepositoryUrl("http://test.com/test/test");
        ReflectionTestUtils.setField(detail, "filename", "I_am_a_file");
        LPVSFileUtil.saveGithubDiffs(
                new ArrayList<GHPullRequestFileDetail>() {
                    {
                        add(detail);
                    }
                },
                webhookConfig);
        ReflectionTestUtils.setField(detail, "patch", "");
        Assertions.assertFalse(
                LPVSFileUtil.saveGithubDiffs(
                                new ArrayList<GHPullRequestFileDetail>() {
                                    {
                                        add(detail);
                                    }
                                },
                                webhookConfig)
                        .contains("Projects//aaaa"));
    }

    @Test
    public void testGetLocalDirectoryPathWithHeadCommitSHA() {
        LPVSQueue mockWebhookConfig = Mockito.mock(LPVSQueue.class);
        Mockito.when(mockWebhookConfig.getHeadCommitSHA()).thenReturn("abcdef123");

        try (MockedStatic<LPVSPayloadUtil> mocked_static_file_util =
                mockStatic(LPVSPayloadUtil.class)) {
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getRepositoryName(Mockito.any()))
                    .thenReturn("repoName");

            String result = LPVSFileUtil.getLocalDirectoryPath(mockWebhookConfig);
            String expectedPath =
                    System.getProperty("user.home")
                            + File.separator
                            + "Projects"
                            + File.separator
                            + "repoName"
                            + File.separator
                            + "abcdef123";
            assert (result.equals(expectedPath));
        }
    }

    @Test
    public void testGetLocalDirectoryPathWithHeadCommitSHAEmpty() {
        LPVSQueue mockWebhookConfig = Mockito.mock(LPVSQueue.class);
        Mockito.when(mockWebhookConfig.getHeadCommitSHA()).thenReturn("");

        try (MockedStatic<LPVSPayloadUtil> mocked_static_file_util =
                mockStatic(LPVSPayloadUtil.class)) {
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getRepositoryName(Mockito.any()))
                    .thenReturn("repoName");
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getPullRequestId(Mockito.any()))
                    .thenReturn("1");

            String result = LPVSFileUtil.getLocalDirectoryPath(mockWebhookConfig);
            String expectedPath =
                    System.getProperty("user.home")
                            + File.separator
                            + "Projects"
                            + File.separator
                            + "repoName"
                            + File.separator
                            + "1";
            assert (result.equals(expectedPath));
        }
    }

    @Test
    public void testGetLocalDirectoryPathWithoutHeadCommitSHA() {
        LPVSQueue mockWebhookConfig = Mockito.mock(LPVSQueue.class);
        Mockito.when(mockWebhookConfig.getHeadCommitSHA()).thenReturn(null);

        try (MockedStatic<LPVSPayloadUtil> mocked_static_file_util =
                mockStatic(LPVSPayloadUtil.class)) {
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getRepositoryName(Mockito.any()))
                    .thenReturn("repoName");
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getPullRequestId(Mockito.any()))
                    .thenReturn("pullRequestId");

            String result = LPVSFileUtil.getLocalDirectoryPath(mockWebhookConfig);
            String expectedPath =
                    System.getProperty("user.home")
                            + File.separator
                            + "Projects"
                            + File.separator
                            + "repoName"
                            + File.separator
                            + "pullRequestId";
            assert (result.equals(expectedPath));
        }
    }

    @Test
    public void testGetScanResultsJsonFilePathWithHeadCommitSHA() {
        LPVSQueue mockWebhookConfig = Mockito.mock(LPVSQueue.class);
        Mockito.when(mockWebhookConfig.getHeadCommitSHA()).thenReturn("abcdef123");

        try (MockedStatic<LPVSPayloadUtil> mocked_static_file_util =
                mockStatic(LPVSPayloadUtil.class)) {
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getRepositoryName(Mockito.any()))
                    .thenReturn("repoName");

            String result = LPVSFileUtil.getScanResultsJsonFilePath(mockWebhookConfig);
            String expectedPath =
                    System.getProperty("user.home")
                            + File.separator
                            + "Results"
                            + File.separator
                            + "repoName"
                            + File.separator
                            + "abcdef123.json";
            assert (result.equals(expectedPath));
        }
    }

    @Test
    public void testGetScanResultsJsonFilePathWithHeadCommitSHAEmpty() {
        LPVSQueue mockWebhookConfig = Mockito.mock(LPVSQueue.class);
        Mockito.when(mockWebhookConfig.getHeadCommitSHA()).thenReturn("");

        try (MockedStatic<LPVSPayloadUtil> mocked_static_file_util =
                mockStatic(LPVSPayloadUtil.class)) {
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getRepositoryName(Mockito.any()))
                    .thenReturn("repoName");
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getPullRequestId(Mockito.any()))
                    .thenReturn("1");

            String result = LPVSFileUtil.getScanResultsJsonFilePath(mockWebhookConfig);
            String expectedPath =
                    System.getProperty("user.home")
                            + File.separator
                            + "Results"
                            + File.separator
                            + "repoName"
                            + File.separator
                            + "1.json";
            assert (result.equals(expectedPath));
        }
    }

    @Test
    public void testGetScanResultsJsonFilePathWithoutHeadCommitSHA() {
        LPVSQueue mockWebhookConfig = Mockito.mock(LPVSQueue.class);
        Mockito.when(mockWebhookConfig.getHeadCommitSHA()).thenReturn(null);

        try (MockedStatic<LPVSPayloadUtil> mocked_static_file_util =
                mockStatic(LPVSPayloadUtil.class)) {
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getRepositoryName(Mockito.any()))
                    .thenReturn("repoName");
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getPullRequestId(Mockito.any()))
                    .thenReturn("pullRequestId");

            String result = LPVSFileUtil.getScanResultsJsonFilePath(mockWebhookConfig);
            String expectedPath =
                    System.getProperty("user.home")
                            + File.separator
                            + "Results"
                            + File.separator
                            + "repoName"
                            + File.separator
                            + "pullRequestId.json";
            assert (result.equals(expectedPath));
        }
    }

    @Test
    public void testGetScanResultsDirectoryPath() {
        LPVSQueue mockWebhookConfig = Mockito.mock(LPVSQueue.class);
        try (MockedStatic<LPVSPayloadUtil> mocked_static_file_util =
                mockStatic(LPVSPayloadUtil.class)) {
            mocked_static_file_util
                    .when(() -> LPVSPayloadUtil.getRepositoryName(Mockito.any()))
                    .thenReturn("repoName");
            String result = LPVSFileUtil.getScanResultsDirectoryPath(mockWebhookConfig);
            String expectedPath =
                    System.getProperty("user.home")
                            + File.separator
                            + "Results"
                            + File.separator
                            + "repoName";
            assert (result.equals(expectedPath));
        }
    }

    @Test
    public void testSaveFileWithEmptyPatchedLines() {
        String fileName = "testFile.txt";
        String directoryPath = "testDirectory";
        List<String> patchedLines = new ArrayList<>();

        LPVSFileUtil.saveFile(fileName, directoryPath, patchedLines);
        Boolean result1 = Files.exists(Paths.get(directoryPath, fileName));
        assert (result1.equals(false));

        LPVSFileUtil.saveFile(fileName, directoryPath, null);
        Boolean result2 = Files.exists(Paths.get(directoryPath, fileName));
        assert (result2.equals(false));
    }
}
