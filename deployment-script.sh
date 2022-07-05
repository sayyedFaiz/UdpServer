#!/usr/bin/env bash

# Generate ssh key on marvin host
# ssh-keygen -t rsa # no file name, no password
# repeat process for all raspberry pi's from marvin host
# ssh-copy-id -i team<number>@marvin.informatik.uni-stuttgart.de

HOSTS=("129.69.210.152" "129.69.210.174" "129.69.210.178" "129.69.210.196" "129.69.210.197")
TEAM_USER="team11"
FILE_TO_COMPILE_EXECUTE="Server.java"
echo "##### Starting Deployement ######"

for host in "${HOSTS[@]}"
do
    for file in ${@}
    do
        echo " ++ Transfering ${file} to ${host}"
        scp $file ${TEAM_USER}@${host}:~/
    done
    # echo " ++ Compiling ${FILE_TO_COMPILE_EXECUTE} on ${host}"
    # if [[ "$host" != "129.69.210.174" ]]
    then
        # ssh ${TEAM_USER}@${host} 'javac Server.java'
        # java $(cut -d'.' -f1 <<<${FILE_TO_COMPILE_EXECUTE})&"
    fi
done
echo " ++ Successfully transfered and executed files on all defined hosts"