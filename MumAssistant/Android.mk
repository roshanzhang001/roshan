#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 baidumapapi_v3_0_0 locSDK_3.1
LOCAL_JNI_SHARED_LIBRARIES := libBaiduMapSDK_v3_0_0 liblocSDK3
LOCAL_SDK_VERSION := current

LOCAL_PACKAGE_NAME := MumAssistant
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := baidumapapi_v3_0_0:libs/baidumapapi_v3_0_0.jar locSDK_3.1:libs/locSDK_3.1.jar

LOCAL_PREBUILT_LIBS := libs/armeabi/libBaiduMapSDK_v3_0_0.so \
			libs/armeabi/liblocSDK3.so
include $(BUILD_MULTI_PREBUILT)

