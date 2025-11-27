# GDiolitsis Engine Lab (GEL) — Author & Developer
# CMakeLists for cpustat native lib (v13.1 – fixed flags)

cmake_minimum_required(VERSION 3.22.1)

project(cpustat C)

# ------------------------------------------
# Correct flag for C code (NOT CXX!)
# ------------------------------------------
set(CMAKE_C_FLAGS "-std=c11")

add_library(
    cpustat
    SHARED
    cpustat.c
)

find_library(
    log-lib
    log
)

target_link_libraries(
    cpustat
    ${log-lib}
)
