# -*- encoding: utf-8 -*-
# stub: asciidoctor-kroki 0.5.0 ruby lib

Gem::Specification.new do |s|
  s.name = "asciidoctor-kroki".freeze
  s.version = "0.5.0"

  s.required_rubygems_version = Gem::Requirement.new(">= 0".freeze) if s.respond_to? :required_rubygems_version=
  s.metadata = { "bug_tracker_uri" => "https://github.com/Mogztter/asciidoctor-kroki/issues", "source_code_uri" => "https://github.com/Mogztter/asciidoctor-kroki" } if s.respond_to? :metadata=
  s.require_paths = ["lib".freeze]
  s.authors = ["Guillaume Grossetie".freeze]
  s.date = "2021-07-05"
  s.description = "An extension for Asciidoctor to convert diagrams to images using https://kroki.io".freeze
  s.email = ["ggrossetie@yuzutech.fr".freeze]
  s.homepage = "https://github.com/Mogztter/asciidoctor-kroki".freeze
  s.licenses = ["MIT".freeze]
  s.rubygems_version = "2.7.10".freeze
  s.summary = "Asciidoctor extension to convert diagrams to images using Kroki".freeze

  s.installed_by_version = "2.7.10" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<asciidoctor>.freeze, ["~> 2.0"])
      s.add_development_dependency(%q<rake>.freeze, ["~> 12.3.2"])
      s.add_development_dependency(%q<rspec>.freeze, ["~> 3.8.0"])
      s.add_development_dependency(%q<rubocop>.freeze, ["~> 0.74.0"])
    else
      s.add_dependency(%q<asciidoctor>.freeze, ["~> 2.0"])
      s.add_dependency(%q<rake>.freeze, ["~> 12.3.2"])
      s.add_dependency(%q<rspec>.freeze, ["~> 3.8.0"])
      s.add_dependency(%q<rubocop>.freeze, ["~> 0.74.0"])
    end
  else
    s.add_dependency(%q<asciidoctor>.freeze, ["~> 2.0"])
    s.add_dependency(%q<rake>.freeze, ["~> 12.3.2"])
    s.add_dependency(%q<rspec>.freeze, ["~> 3.8.0"])
    s.add_dependency(%q<rubocop>.freeze, ["~> 0.74.0"])
  end
end
