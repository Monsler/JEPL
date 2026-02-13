#[derive(PartialEq, Debug)]
pub enum Command<'a> {
    CommandPrint(Vec<&'a str>),
    CommandPrintln(Vec<&'a str>),
}