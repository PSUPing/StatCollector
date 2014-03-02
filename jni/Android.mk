LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := systemcalls
LOCAL_SRC_FILES := systemcalls.c

include $(BUILD_SHARED_LIBRARY)