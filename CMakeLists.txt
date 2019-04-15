cmake_minimum_required(VERSION 3.4.1)

#set path to oboe library https://github.com/google/oboe
set (OBOE_DIR /home/patrick-gorman/Documents/libraries/oboe)

add_Subdirectory ( ${OBOE_DIR} ./oboe)
indclude_Subdirectories ( ${OBOE_DIR} ./oboe)
target_link_libraries (native-lib log oboe)