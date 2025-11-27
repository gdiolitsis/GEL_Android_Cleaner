# GDiolitsis Engine Lab (GEL) — Author & Developer
# CMakeLists for cpustat native lib

cmake_minimum_required(VERSION 3.22.1)

project(cpustat)

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
