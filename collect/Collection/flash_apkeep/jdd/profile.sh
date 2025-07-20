#!/bin/sh
#
# script for quickl running some big examples...
#

set -e

if [ $# -eq 0 ] ; then
    echo "Valid targets are: bdd, zdd, trace"
    exit 20
fi

./gradlew build

export J="java -cp build/classes/java/main -Xmx512M -Xms2M"

for arg in "$@"
do
    case $arg in

		"bdd" )
            $J jdd.examples.BDDQueens 7 7 7 8 8 9 9 10 10 11 11 12 12
            # java -Xmx256M -Xms128M jdd.examples.BDDQueens 13
            $J jdd.examples.Adder 16
            $J jdd.examples.Adder 32
            $J jdd.examples.Adder 64
            $J jdd.examples.Adder 128
            $J jdd.examples.Adder 256
            $J jdd.examples.Adder 512
            $J jdd.examples.Adder 1024
            $J jdd.examples.Milner 16
            $J jdd.examples.Milner 32
            $J jdd.examples.Milner 48
            $J jdd.examples.Milner 56
            $J jdd.examples.Milner 64
            $J jdd.examples.Milner 72

            ;;
		"zdd" )
            $J jdd.examples.ZDDQueens 7 7 7 8 8 9 9 10 10 11 11 12 12 13 13
            $J jdd.examples.ZDDCSPQueens 7 7 7 8 8 9 9 10 10 11 11 12 12 13 13
        ;;


        "trace" )
            echo run the traces
            $J jdd.bdd.debug.BDDTraceSuite data/yangs_traces.zip 10240 > build/jdd_yangs_traces.txt
            $J jdd.bdd.debug.BDDTraceSuite data/velev_sss.zip 200000 > build/jdd_sss_traces.txt
            $J jdd.bdd.debug.BDDTraceSuite data/iscas_rest.zip 100000 || echo Failed > build/jdd_ISCAS85_traces.txt
            $J jdd.bdd.debug.BDDTraceSuite data/iscas_c6288.zip 500000 || echo Failed >> build/jdd_ISCAS85_traces.txt
            ;;


        *)
            echo "Unknown target"
            exit 20
            ;;
   esac
done
