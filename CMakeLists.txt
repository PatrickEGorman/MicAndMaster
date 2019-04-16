cmake_minimum_required(VERSION 3.4.1)

#set path to oboe library https://github.com/google/oboe
set (OBOE_DIR /home/patrick-gorman/Documents/libraries/oboe)

#link c++ files
#file(GLOB native_lib_src   "app/src/main/cpp/native_lib.cpp")
add_library( native-lib SHARED app/src/main/cpp/native_lib.cpp app/src/main/cpp/AudioProcessor.cpp)

add_subdirectory ( ${OBOE_DIR} ./oboe)
include_directories ( ${OBOE_DIR} ./include)
target_link_libraries (native-lib log oboe)


