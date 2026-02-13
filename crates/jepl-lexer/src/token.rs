use std::fmt::Display;

#[derive(Debug, PartialEq)]
pub enum TokenType {
    PLUS,
    MINUS,
    DIV,
    MUL,
    STR,
    NUMBER,
    LBRACE,
    RBRACE,
    RPAREN,
    LPAREN,
    RBRACKET,
    LBRACKET,
    COLON,
    COMMA
}

pub struct Token<'token> {
    pub value: &'token str,
    pub position: usize,
    pub token_type: TokenType
}

impl<'token> Display for Token<'token> {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Token (pos: {}): {} ({:#?})", self.position, self.value, self.token_type)
    }
}