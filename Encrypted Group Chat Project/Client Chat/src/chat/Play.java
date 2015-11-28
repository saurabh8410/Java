package chat;

class Play {

    private final char originalMat[][] = {
        {'A', 'B', 'C', 'D', 'E', '@', '{', '8'},
        {'F', 'G', 'H', 'I', 'K', '^', '}', '9'},
        {'L', 'M', 'N', 'O', 'P', ')', '[', '~'},
        {'Q', 'R', 'S', 'T', 'U', ']', '+', '`'},
        {'V', 'W', 'X', 'Y', 'Z', '_', '?', '|'},
        {'$', '!', '%', '&', '*', '(', ';', '.'},
        {'<', '>', '=', '-', ':', '\'', '"', '\''},
        {'0', '1', '2', '3', '4', '5', '6', '7'}
    };
    private char mat[][] = {
        {'A', 'B', 'C', 'D', 'E', '@', '{', '8'},
        {'F', 'G', 'H', 'I', 'K', '^', '}', '9'},
        {'L', 'M', 'N', 'O', 'P', ')', '[', '~'},
        {'Q', 'R', 'S', 'T', 'U', ']', '+', '`'},
        {'V', 'W', 'X', 'Y', 'Z', '_', '?', '|'},
        {'$', '!', '%', '&', '*', '(', ';', '.'},
        {'<', '>', '=', '-', ':', '\'', '"', '\''},
        {'0', '1', '2', '3', '4', '5', '6', '7'}
    };
    String sp = "";
    String en = "";

    String encryption(String msg) {
        char f, s, c1 = '0', c2 = '0';
        int f_row_pos = 0, s_row_pos = 0;
        int f_col_pos = 0, s_col_pos = 0;
        if (msg.length() % 2 != 0) {
            msg += '`';
        }

        //loop to find space position
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                sp += "1";
            } else {
                sp += "0";
            }
        }

        for (int i = 0; i < msg.length(); i += 2) {
            f = Character.toUpperCase(msg.charAt(i));
            s = Character.toUpperCase(msg.charAt(i + 1));
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    if (mat[j][k] == f) {
                        f_row_pos = j;
                        f_col_pos = k;
                    }
                    if (mat[j][k] == s) {
                        s_row_pos = j;
                        s_col_pos = k;
                    }
                }
            }

            if (f_row_pos == s_row_pos) {
                c1 = mat[f_row_pos][(f_col_pos + 1) % 8];
                c2 = mat[s_row_pos][(s_col_pos + 1) % 8];
            } else if (f_col_pos == s_col_pos) {
                c1 = mat[(f_row_pos + 1) % 8][f_col_pos];
                c2 = mat[(s_row_pos + 1) % 8][s_col_pos];
            } else {
                c1 = mat[f_row_pos][s_col_pos];
                c2 = mat[s_row_pos][f_col_pos];
            }
            en = en + "" + Character.toUpperCase(c1) + "" + Character.toUpperCase(c2);
        }
        return en;
    }

    String decryption(String msg) {
        String original = "", temp = "";
        char f = '`', s = '`', p1 = '0', p2 = '0';
        int f_row_pos = 0, s_row_pos = 0;
        int f_col_pos = 0, s_col_pos = 0;
        for (int i = 0; i < msg.length(); i += 2) { //loop to find the position of character on matrix
            f = msg.charAt(i);
            s = msg.charAt(i + 1);
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    if (mat[j][k] == f) {
                        f_row_pos = j;
                        f_col_pos = k;
                    }
                    if (mat[j][k] == s) {
                        s_row_pos = j;
                        s_col_pos = k;
                    }
                }
            }

            /* Finding intersecting point to find cipher */
            if (f_row_pos == s_row_pos) {
                if (f_col_pos - 1 < 0) {
                    f_col_pos = f_col_pos - 1 + 8;
                } else {
                    f_col_pos--;
                }
                if (s_col_pos - 1 < 0) {
                    s_col_pos = s_col_pos - 1 + 8;
                } else {
                    s_col_pos--;
                }

                p1 = mat[f_row_pos][f_col_pos];
                p2 = mat[s_row_pos][s_col_pos];
            } else if (f_col_pos == s_col_pos) {
                if (f_row_pos - 1 < 0) {
                    f_row_pos = f_row_pos - 1 + 8;
                } else {
                    f_row_pos--;
                }
                if (s_row_pos - 1 < 0) {
                    s_row_pos = s_row_pos - 1 + 8;
                } else {
                    s_row_pos--;
                }

                p1 = mat[f_row_pos][f_col_pos];
                p2 = mat[s_row_pos][s_col_pos];
            } else {
                p1 = mat[f_row_pos][s_col_pos];
                p2 = mat[s_row_pos][f_col_pos];
            }
            temp = temp + "" + Character.toLowerCase(p1) + "" + Character.toLowerCase(p2);
        }

        //loop to put space at their right position
        for (int i = 0; i < temp.length(); i++) {
            if (sp.charAt(i) == '1') {
                original += " ";
            } else if ((i == temp.length() - 1) && (temp.charAt(i) == '`')) {
            } else {
                original += "" + temp.charAt(i);
            }
        }
        return original;
    }

    void setKey(String key) {

        String m_key = "";//modified key
        int flag = 0, check_i_j = 0;

        //loop to find the repeating character and then remove it
        for (int i = 0; i < key.length(); i++) {
            if (Character.toLowerCase(key.charAt(i)) == 'i' || Character.toLowerCase(key.charAt(i)) == 'j') {
                check_i_j++;
            }
            for (int j = i - 1; j >= 0; j--) {
                if (Character.toLowerCase(key.charAt(i)) == Character.toLowerCase(key.charAt(j))) {
                    flag = 1;
                }
            }
            if (flag != 1 && !((check_i_j >= 2) && (Character.toLowerCase(key.charAt(i)) == 'i' || Character.toLowerCase(key.charAt(i)) == 'j'))) {
                m_key += Character.toUpperCase(key.charAt(i));
            }
            flag = 0;
        }

        System.out.println(m_key);

        //loop that put key value in matrix
        for (int j = 0, c = 0, i = 0; c < m_key.length(); i++, c++) {
            if (j == 8) {
                j = 0;
                i++;
            }
            mat[j][i] = m_key.charAt(c);
        }

        flag = 0;
        boolean temp = false;
        for (int i = 0, k = 0, l = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0 && j == m_key.length()) {
                    temp = true;
                }
                if (temp) {
                    out:
                    for (; k < 8;) {
                        for (; l < 8;) {
                            //System.out.println(m_key.contains(""+originalMat[k][l]) + "   " + originalMat[k][l] + "  "+i+"  "+j);
                            if (!m_key.contains("" + originalMat[k][l])) {
                                mat[i][j] = originalMat[k][l++];
                                //System.out.println(mat[i][j]);
                                if (l >= 8) {
                                    k++;
                                    l = 0;
                                }
                                break out;
                            }
                            l++;
                            j--;
                            if (l >= 8) {
                                k++;
                                l = 0;
                            }
                            break out;
                        }
                    }
                }
            }
        }
    }

    /*public static void main(String[] args) {    
     String m_key = "";//modified key
     int flag = 0, check_i_j = 0;

     //loop to find the repeating character and then remove it
     for (int i = 0; i < key.length(); i++) {
     if (Character.toLowerCase(key.charAt(i)) == 'i' || Character.toLowerCase(key.charAt(i)) == 'j') {
     check_i_j++;
     }
     for (int j = i - 1; j >= 0; j--) {
     if (Character.toLowerCase(key.charAt(i)) == Character.toLowerCase(key.charAt(j))) {
     flag = 1;
     }
     }
     if (flag != 1 && !((check_i_j >= 2) && (Character.toLowerCase(key.charAt(i)) == 'i' || Character.toLowerCase(key.charAt(i)) == 'j'))) {
     m_key += Character.toUpperCase(key.charAt(i));
     }
     flag = 0;
     }

     System.out.println(m_key);

     //loop that put key value in matrix
     for (int j = 0, c = 0, i = 0; c < m_key.length(); i++, c++) {
     if (j == 8) {
     j = 0;
     i++;
     }
     mat[j][i] = m_key.charAt(c);
     }

     flag = 0;
     boolean temp = false;
     for (int i = 0, k = 0, l = 0; i < 8; i++) {
     for (int j = 0; j < 8; j++) {
     if (i == 0 && j == m_key.length()) {
     temp = true;
     }
     if (temp) {
     out:
     for (; k < 8;) {
     for (; l < 8;) {
     //System.out.println(m_key.contains(""+originalMat[k][l]) + "   " + originalMat[k][l] + "  "+i+"  "+j);
     if (!m_key.contains("" + originalMat[k][l])) {
     mat[i][j] = originalMat[k][l++];
     System.out.println(mat[i][j]);
     if (l >= 8) {
     k++;
     l = 0;
     }
     break out;
     }
     l++;
     j--;
     if (l >= 8) {
     k++;
     l = 0;
     }
     break out;
     }
     }
     }
     }
     }

        
     for(int i=0;i<8;i++){
     for(int j=0;j<8;j++){
     System.out.print(" "+Character.toUpperCase(mat[i][j]));
     }
     System.out.println();
     }
         
     }*/
}
