#! /bin/bash

_usage() {
    echo "USAGE: $0 -p TYPE1 -n TOTAL [-i INPUTFILE]"
}

INPUTFILE="input.txt"
TOTAL=""
TYPE1=""

while [[ $# > 1 ]]
do
    key="$1"
    case $key in 
        -p)
            TYPE1="$2"
            shift
            ;;
        -n)
            TOTAL="$2"
            shift
            ;;
        -i)
            INPUTFILE="$2"
            shift
            ;;
        *)
            _usage
            exit
    esac
    shift
done

if [[ -z $TYPE1 ]] 
then
    _usage
    exit
fi

if [[ -z $TOTAL ]]
then
    _usage
    exit
fi

if [[ ! -r $INPUTFILE ]] 
then
    _usage
    exit
fi

for i in `seq $TOTAL`
do
    rm -f $INPUTFILE.$i
    touch $INPUTFILE.$i
done

for i in `seq $TYPE1`
do
    cat $INPUTFILE | while read line
    do
        echo $i:$line >> "$INPUTFILE.$i"
    done
done

for i in `seq $TOTAL`
do
    ./MyServer $i $TOTAL -i $INPUTFILE.$i
done
