#
# Copyright 2009 Cedric Priscal
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License. 
#

LOCAL_PATH := $(call my-dir)
MY_LOCAL_PATH := $(LOCAL_PATH)

include $(CLEAR_VARS)
#将Hpsam编译成静态库，供fxjni.c调用
#Hpsam.a在工程目录的obj文件夹下生成
LOCAL_SRC_FILES := Hpsam.c sptc/client.cpp sptc/sptc_reader_api.c
LOCAL_SHARED_LIBRARIES := libdevapi
LOCAL_LDLIBS += -llog
LOCAL_PREBUILT_LIBS := $(LOCAL_PATH)/libdevapi.so
LOCAL_LDFLAGS := $(LOCAL_PATH)/libdevapi.so
LOCAL_MODULE    := libHpsam
#include $(BUILD_SHARED_LIBRARY)
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)

#LOCAL_PRELINK_MODULE :=false
LOCAL_SRC_FILES := fxjni.c
LOCAL_SHARED_LIBRARIES := libdevapi
LOCAL_PREBUILT_LIBS := $(LOCAL_PATH)/libdevapi.so
LOCAL_LDFLAGS := $(LOCAL_PATH)/libdevapi.so
#LOCAL_SHARED_LIBRARIES := libHpsam
#LOCAL_PREBUILT_LIBS := $(LOCAL_PATH)/libHpsam.so
#LOCAL_LDFLAGS := $(LOCAL_PATH)/libHpsam.so

LOCAL_MODULE := fxjni
LOCAL_LDLIBS += -llog
LOCAL_STATIC_LIBRARIES := libHpsam
LOCAL_PATH := $(MY_LOCAL_PATH)

include $(BUILD_SHARED_LIBRARY)

#公共ku
