cmake_minimum_required(VERSION 3.4.1)

add_library(price_validation SHARED validate_price.cpp)

find_library(log-lib log)

target_link_libraries(price_validation ${log-lib})