require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-radaeepdf"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "10.0" }
  s.source       = { :git => "https://github.com/hamidfzm/react-native-radaeepdf.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.*"
  s.prefix_header_file = 'ios/PDFViewer/PDFViewer-Prefix.pch'
  s.preserve_paths = 'ios/PDFViewer/PDFLib/.a'
  s.xcconfig = {
    'OTHER_LDFLAGS' => '-framework QuartzCore -framework CoreGraphics -lRDPDFLib -lstdc++ -lz',
    "LIBRARY_SEARCH_PATHS" => "#{File.join(File.dirname(__FILE__), 'ios/PDFViewer/PDFLib')}",
  }
  s.library = 'c++'

  s.vendored_libraries = 'libRDPDFLib'

  s.dependency "React-Core"
end
