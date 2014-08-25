#!/bin/sh

# Firestarter
#
# Copyright 2014 Greg Kopff
# All rights reserved.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

#
# Sets the internal field separator to a newline.
#
setToNewline() {
  IFS=$'\n'
}

#
# Restores the internal field separator to its default value.
#
setToNormal() {
  unset IFS
}

FS_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

FS_JAR=`find $FS_HOME -name firestarter-\*.jar -print | sort | tail -1`
if [ -z "$FS_JAR" ]; then
  echo "Could not find firestarter JAR"
  exit 1
fi

if [ $# -ne 1 ]; then
  echo "Usage: $0 config"
  exit 1
fi
CONF=$1
SESSION=`basename $CONF .conf`

set -f                                           # globbing off

DIRECTIVES="`java -Xms64M -Xmx64M -jar $FS_JAR $CONF`"
if [ $? -ne 0 ]; then
  exit 1
fi

tmux has-session -t $SESSION                     # 0: sessions exist; 1: sessions don't exist
if (( $? == 1 )); then
  echo "Creating new session for $SESSION"
  tmux new -s $SESSION -d                        # create a new session and detach
  tmux set -t $SESSION set-remain-on-exit on     # don't remove exited processes
fi

setToNewline                                     # for directive processing, split by newine
for DIRECTIVE in $DIRECTIVES; do
  setToNormal                                    # return to default field separation

  echo "Directive: $DIRECTIVE"
  tmux new-window -aP -t $SESSION "$DIRECTIVE"   # execute directive in a new tmux window

  setToNewline                                   # (prepare for next directive)
done
setToNormal                                      # (clean up)
