# GDiolitsis Engine Lab (GEL) — Author & Developer
# CMakeLists.txt — FINAL v16.0
# Includes:
#   - cpustat.c   (CPU Triple Engine)
#   - corefreq.c  (Per-core monitor)

cmake_minimum_required(VERSION 3.10.2)

project("gel_native")

# =======================================================
# cpustat (RAW + FREQ + THERMAL engine)
# =======================================================
add_library(cpustat SHARED cpustat.c)

target_link_libraries(cpustat
        android
        log)

# =======================================================
# corefreq (Per-Core Hz + Status)
# =======================================================
add_library(corefreq SHARED corefreq.c)

target_link_libraries(corefreq
        android
        log)
