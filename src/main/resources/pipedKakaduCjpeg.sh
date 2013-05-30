#!/bin/bash
FORMAT=bmp
CJPEG="/usr/bin/cjpeg"
KDU_EXPAND="/disk1/kakadu_v6_3_1-01018N/bin/Linux-x86-64/kdu_expand"
export LD_LIBRARY_PATH=/disk1/kakadu_v6_3_1-01018N/lib/Linux-x86-64/
INPUT=$1
OUTPUT=$2
while [ "$3" ]
do
    PARAMS="$PARAMS $3"
    shift 1
done
mkfifo /tmp/cjpeg$$.$FORMAT
$CJPEG /tmp/cjpeg$$.$FORMAT > $OUTPUT &
$KDU_EXPAND -double_buffering 4 -num_threads 3 -i $INPUT -o /tmp/cjpeg$$.$FORMAT $PARAMS
rm /tmp/cjpeg$$.$FORMAT